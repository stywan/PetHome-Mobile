package com.example.pethome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pethome.data.model.Pet
import com.example.pethome.repository.PetRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PetFormState(
    val name: String = "",
    val species: String = "",
    val breed: String = "",
    val age: String = "",
    val weight: String = "",
    val gender: String = "",
    val color: String = "",
    val imageUrl: String? = null,
    val nameError: String? = null,
    val speciesError: String? = null,
    val breedError: String? = null,
    val ageError: String? = null,
    val weightError: String? = null,
    val genderError: String? = null,
    val colorError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class PetViewModel(
    private val petRepository: PetRepository,
    private val userId: String
) : ViewModel() {

    private val _formState = MutableStateFlow(PetFormState())
    val formState: StateFlow<PetFormState> = _formState.asStateFlow()

    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val pets: StateFlow<List<Pet>> = _pets.asStateFlow()

    private val _editingPet = MutableStateFlow<Pet?>(null)
    val editingPet: StateFlow<Pet?> = _editingPet.asStateFlow()

    private var loadPetsJob: Job? = null

    init {
        loadPets()
    }

    private fun loadPets() {
        // Cancelar el job anterior si existe
        loadPetsJob?.cancel()

        loadPetsJob = viewModelScope.launch {
            try {
                // Llamar directamente a la función suspend
                val petList = petRepository.getPetsByUser(userId)
                _pets.value = petList
            } catch (e: Exception) {
                // Si hay error al cargar, mantener lista vacía
                _pets.value = emptyList()
            }
        }
    }

    fun refreshPets() {
        loadPets()
    }

    fun onNameChange(name: String) {
        _formState.update { it.copy(name = name, nameError = null) }
    }

    fun onSpeciesChange(species: String) {
        _formState.update { it.copy(species = species, speciesError = null) }
    }

    fun onBreedChange(breed: String) {
        _formState.update { it.copy(breed = breed, breedError = null) }
    }

    fun onAgeChange(age: String) {
        _formState.update { it.copy(age = age, ageError = null) }
    }

    fun onWeightChange(weight: String) {
        _formState.update { it.copy(weight = weight, weightError = null) }
    }

    fun onGenderChange(gender: String) {
        _formState.update { it.copy(gender = gender, genderError = null) }
    }

    fun onColorChange(color: String) {
        _formState.update { it.copy(color = color, colorError = null) }
    }

    fun onImageSelected(imageUri: String?) {
        _formState.update { it.copy(imageUrl = imageUri) }
    }

    fun clearError() {
        _formState.update { it.copy(errorMessage = null) }
    }

    fun startEditingPet(pet: Pet) {
        _editingPet.value = pet
        _formState.update {
            PetFormState(
                name = pet.name,
                species = pet.species,
                breed = pet.breed,
                age = pet.age.toString(),
                weight = pet.weight.toString(),
                gender = pet.gender,
                color = pet.color,
                imageUrl = pet.imageUrl
            )
        }
    }

    fun clearForm() {
        _editingPet.value = null
        _formState.value = PetFormState()
    }

    fun resetSuccessState() {
        _formState.update { it.copy(isSuccess = false) }
    }

    fun savePet() {
        if (!validateForm()) {
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true) }

            try {
                val pet = Pet(
                    id = _editingPet.value?.id ?: "",
                    name = _formState.value.name,
                    species = _formState.value.species,
                    breed = _formState.value.breed,
                    age = _formState.value.age.toInt(),
                    weight = _formState.value.weight.toDouble(),
                    gender = _formState.value.gender,
                    color = _formState.value.color,
                    imageUrl = _formState.value.imageUrl,
                    userId = userId
                )

                val result = if (_editingPet.value != null) {
                    petRepository.updatePet(pet)
                } else {
                    petRepository.addPet(pet)
                }

                result.fold(
                    onSuccess = {
                        _formState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                errorMessage = null
                            )
                        }
                        // Recargar la lista de mascotas después de guardar
                        loadPets()
                    },
                    onFailure = { exception ->
                        _formState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "Error al guardar mascota"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _formState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error inesperado: ${e.message}"
                    )
                }
            }
        }
    }

    fun deletePet(petId: String) {
        viewModelScope.launch {
            try {
                val result = petRepository.deletePet(petId)
                result.fold(
                    onSuccess = {
                        // Actualizar la lista localmente de inmediato para mejor UX
                        _pets.value = _pets.value.filter { it.id != petId }
                        // También recargar desde el servidor para asegurar consistencia
                        loadPets()
                    },
                    onFailure = { exception ->
                        _formState.update {
                            it.copy(errorMessage = "Error al eliminar mascota: ${exception.message}")
                        }
                    }
                )
            } catch (e: Exception) {
                _formState.update {
                    it.copy(errorMessage = "Error al eliminar mascota: ${e.message}")
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (_formState.value.name.isBlank()) {
            _formState.update { it.copy(nameError = "El nombre es requerido") }
            isValid = false
        }

        if (_formState.value.species.isBlank()) {
            _formState.update { it.copy(speciesError = "La especie es requerida") }
            isValid = false
        }

        if (_formState.value.breed.isBlank()) {
            _formState.update { it.copy(breedError = "La raza es requerida") }
            isValid = false
        }

        if (_formState.value.age.isBlank()) {
            _formState.update { it.copy(ageError = "La edad es requerida") }
            isValid = false
        } else {
            try {
                val age = _formState.value.age.toInt()
                if (age < 0 || age > 50) {
                    _formState.update { it.copy(ageError = "La edad debe estar entre 0 y 50") }
                    isValid = false
                }
            } catch (e: NumberFormatException) {
                _formState.update { it.copy(ageError = "La edad debe ser un número") }
                isValid = false
            }
        }

        if (_formState.value.weight.isBlank()) {
            _formState.update { it.copy(weightError = "El peso es requerido") }
            isValid = false
        } else {
            try {
                val weight = _formState.value.weight.toDouble()
                if (weight <= 0 || weight > 200) {
                    _formState.update { it.copy(weightError = "El peso debe estar entre 0 y 200 kg") }
                    isValid = false
                }
            } catch (e: NumberFormatException) {
                _formState.update { it.copy(weightError = "El peso debe ser un número") }
                isValid = false
            }
        }

        if (_formState.value.gender.isBlank()) {
            _formState.update { it.copy(genderError = "El género es requerido") }
            isValid = false
        }

        if (_formState.value.color.isBlank()) {
            _formState.update { it.copy(colorError = "El color es requerido") }
            isValid = false
        }

        return isValid
    }
}

class PetViewModelFactory(
    private val petRepository: PetRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PetViewModel::class.java)) {
            return PetViewModel(petRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
