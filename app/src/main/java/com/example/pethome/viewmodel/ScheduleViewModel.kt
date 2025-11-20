package com.example.pethome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethome.data.model.ScheduleItem
import com.example.pethome.repository.PetRepository
import com.example.pethome.repository.ServiceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ScheduleViewModel(
    private val petRepository: PetRepository,
    private val serviceRepository: ServiceRepository,
    private val userId: String
) : ViewModel() {

    private val _upcomingScheduleItems = MutableStateFlow<List<ScheduleItem>>(emptyList())
    val upcomingScheduleItems: StateFlow<List<ScheduleItem>> = _upcomingScheduleItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadUpcomingItems()
    }

    fun loadUpcomingItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                serviceRepository.getAppointmentsByUser(userId).collect { appointments ->
                    val scheduleItems = mutableListOf<ScheduleItem>()

                    // Convert appointments to schedule items
                    for (appointment in appointments) {
                        // Fetch service and pet details
                        val service = serviceRepository.getServiceById(appointment.serviceId)
                        val pet = petRepository.getPetById(appointment.petId)

                        if (service != null && pet != null) {
                            scheduleItems.add(
                                ScheduleItem.ServiceAppointment(
                                    id = appointment.id,
                                    petId = pet.id,
                                    petName = pet.name,
                                    serviceName = service.name,
                                    serviceCategory = service.category,
                                    serviceDescription = service.shortDescription,
                                    dateTime = appointment.date,
                                    time = appointment.time,
                                    status = appointment.status
                                )
                            )
                        }
                    }

                    // Sort by date/time (most recent first)
                    scheduleItems.sortBy { it.dateTime }

                    _upcomingScheduleItems.value = scheduleItems
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _upcomingScheduleItems.value = emptyList()
            }
        }
    }

    fun refreshSchedule() {
        loadUpcomingItems()
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.Builder().setLanguage("es").setRegion("ES").build())
        return sdf.format(Date(timestamp))
    }
}
