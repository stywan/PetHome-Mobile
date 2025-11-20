package com.example.pethome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pethome.repository.PetRepository
import com.example.pethome.repository.ServiceRepository

class ScheduleViewModelFactory(
    private val petRepository: PetRepository,
    private val serviceRepository: ServiceRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScheduleViewModel(petRepository, serviceRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
