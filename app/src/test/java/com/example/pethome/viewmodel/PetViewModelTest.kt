package com.example.pethome.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.pethome.data.model.Pet
import com.example.pethome.repository.PetRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PetViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var petRepository: PetRepository
    private lateinit var viewModel: PetViewModel
    private val userId = "user123"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        petRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private suspend fun createViewModel() {
        coEvery { petRepository.getPetsByUser(userId) } returns emptyList()
        viewModel = PetViewModel(petRepository, userId)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    // ========== Tests de cambio de estado del formulario ==========

    @Test
    fun `onNameChange updates name and clears error`() = runTest {
        createViewModel()
        val name = "Firulais"

        viewModel.onNameChange(name)

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.name).isEqualTo(name)
            assertThat(state.nameError).isNull()
        }
    }

    @Test
    fun `onSpeciesChange updates species and clears error`() = runTest {
        createViewModel()
        val species = "Perro"

        viewModel.onSpeciesChange(species)

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.species).isEqualTo(species)
            assertThat(state.speciesError).isNull()
        }
    }

    @Test
    fun `onBreedChange updates breed and clears error`() = runTest {
        createViewModel()
        val breed = "Labrador"

        viewModel.onBreedChange(breed)

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.breed).isEqualTo(breed)
            assertThat(state.breedError).isNull()
        }
    }

    @Test
    fun `onAgeChange updates age and clears error`() = runTest {
        createViewModel()
        val age = "5"

        viewModel.onAgeChange(age)

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.age).isEqualTo(age)
            assertThat(state.ageError).isNull()
        }
    }

    @Test
    fun `onWeightChange updates weight and clears error`() = runTest {
        createViewModel()
        val weight = "15.5"

        viewModel.onWeightChange(weight)

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.weight).isEqualTo(weight)
            assertThat(state.weightError).isNull()
        }
    }

    @Test
    fun `onGenderChange updates gender and clears error`() = runTest {
        createViewModel()
        val gender = "Macho"

        viewModel.onGenderChange(gender)

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.gender).isEqualTo(gender)
            assertThat(state.genderError).isNull()
        }
    }

    @Test
    fun `onColorChange updates color and clears error`() = runTest {
        createViewModel()
        val color = "Café"

        viewModel.onColorChange(color)

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.color).isEqualTo(color)
            assertThat(state.colorError).isNull()
        }
    }

    @Test
    fun `onImageSelected updates imageUrl`() = runTest {
        createViewModel()
        val imageUrl = "https://example.com/image.jpg"

        viewModel.onImageSelected(imageUrl)

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.imageUrl).isEqualTo(imageUrl)
        }
    }

    // ========== Tests de validación ==========

    @Test
    fun `savePet with empty name shows error`() = runTest {
        createViewModel()
        viewModel.onNameChange("")
        viewModel.onSpeciesChange("Perro")
        viewModel.onBreedChange("Labrador")
        viewModel.onAgeChange("5")
        viewModel.onWeightChange("15.5")
        viewModel.onGenderChange("Macho")
        viewModel.onColorChange("Café")

        viewModel.savePet()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.nameError).isEqualTo("El nombre es requerido")
        }
    }

    @Test
    fun `savePet with empty species shows error`() = runTest {
        createViewModel()
        viewModel.onNameChange("Firulais")
        viewModel.onSpeciesChange("")
        viewModel.onBreedChange("Labrador")
        viewModel.onAgeChange("5")
        viewModel.onWeightChange("15.5")
        viewModel.onGenderChange("Macho")
        viewModel.onColorChange("Café")

        viewModel.savePet()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.speciesError).isEqualTo("La especie es requerida")
        }
    }

    @Test
    fun `savePet with invalid age shows error`() = runTest {
        createViewModel()
        viewModel.onNameChange("Firulais")
        viewModel.onSpeciesChange("Perro")
        viewModel.onBreedChange("Labrador")
        viewModel.onAgeChange("abc")
        viewModel.onWeightChange("15.5")
        viewModel.onGenderChange("Macho")
        viewModel.onColorChange("Café")

        viewModel.savePet()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.ageError).isEqualTo("La edad debe ser un número")
        }
    }

    @Test
    fun `savePet with age out of range shows error`() = runTest {
        createViewModel()
        viewModel.onNameChange("Firulais")
        viewModel.onSpeciesChange("Perro")
        viewModel.onBreedChange("Labrador")
        viewModel.onAgeChange("60")
        viewModel.onWeightChange("15.5")
        viewModel.onGenderChange("Macho")
        viewModel.onColorChange("Café")

        viewModel.savePet()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.ageError).isEqualTo("La edad debe estar entre 0 y 50")
        }
    }

    @Test
    fun `savePet with invalid weight shows error`() = runTest {
        createViewModel()
        viewModel.onNameChange("Firulais")
        viewModel.onSpeciesChange("Perro")
        viewModel.onBreedChange("Labrador")
        viewModel.onAgeChange("5")
        viewModel.onWeightChange("abc")
        viewModel.onGenderChange("Macho")
        viewModel.onColorChange("Café")

        viewModel.savePet()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.weightError).isEqualTo("El peso debe ser un número")
        }
    }

    @Test
    fun `savePet with weight out of range shows error`() = runTest {
        createViewModel()
        viewModel.onNameChange("Firulais")
        viewModel.onSpeciesChange("Perro")
        viewModel.onBreedChange("Labrador")
        viewModel.onAgeChange("5")
        viewModel.onWeightChange("250")
        viewModel.onGenderChange("Macho")
        viewModel.onColorChange("Café")

        viewModel.savePet()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.weightError).isEqualTo("El peso debe estar entre 0 y 200 kg")
        }
    }

    // ========== Tests de guardar mascota ==========

    @Test
    fun `savePet with valid data succeeds`() = runTest {
        createViewModel()
        val pet = Pet(
            id = "pet1",
            name = "Firulais",
            species = "Perro",
            breed = "Labrador",
            age = 5,
            weight = 15.5,
            gender = "Macho",
            color = "Café",
            imageUrl = null,
            userId = userId
        )

        viewModel.onNameChange("Firulais")
        viewModel.onSpeciesChange("Perro")
        viewModel.onBreedChange("Labrador")
        viewModel.onAgeChange("5")
        viewModel.onWeightChange("15.5")
        viewModel.onGenderChange("Macho")
        viewModel.onColorChange("Café")

        coEvery { petRepository.addPet(match { true }) } returns Result.success(pet)

        viewModel.savePet()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.isSuccess).isTrue()
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorMessage).isNull()
        }
    }

    @Test
    fun `savePet failure shows error message`() = runTest {
        createViewModel()
        viewModel.onNameChange("Firulais")
        viewModel.onSpeciesChange("Perro")
        viewModel.onBreedChange("Labrador")
        viewModel.onAgeChange("5")
        viewModel.onWeightChange("15.5")
        viewModel.onGenderChange("Macho")
        viewModel.onColorChange("Café")

        coEvery { petRepository.addPet(match { true }) } returns Result.failure(Exception("Error de conexión"))

        viewModel.savePet()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.isSuccess).isFalse()
            assertThat(state.errorMessage).contains("Error de conexión")
        }
    }

    // ========== Tests de editar mascota ==========

    @Test
    fun `startEditingPet populates form with pet data`() = runTest {
        createViewModel()
        val pet = Pet(
            id = "pet1",
            name = "Firulais",
            species = "Perro",
            breed = "Labrador",
            age = 5,
            weight = 15.5,
            gender = "Macho",
            color = "Café",
            imageUrl = "https://example.com/image.jpg",
            userId = userId
        )

        viewModel.startEditingPet(pet)

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.name).isEqualTo("Firulais")
            assertThat(state.species).isEqualTo("Perro")
            assertThat(state.breed).isEqualTo("Labrador")
            assertThat(state.age).isEqualTo("5")
            assertThat(state.weight).isEqualTo("15.5")
            assertThat(state.gender).isEqualTo("Macho")
            assertThat(state.color).isEqualTo("Café")
            assertThat(state.imageUrl).isEqualTo("https://example.com/image.jpg")
        }

        viewModel.editingPet.test {
            val editingPet = awaitItem()
            assertThat(editingPet).isEqualTo(pet)
        }
    }

    @Test
    fun `clearForm resets form state`() = runTest {
        createViewModel()
        viewModel.onNameChange("Firulais")
        viewModel.onSpeciesChange("Perro")

        viewModel.clearForm()

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.name).isEmpty()
            assertThat(state.species).isEmpty()
        }

        viewModel.editingPet.test {
            val editingPet = awaitItem()
            assertThat(editingPet).isNull()
        }
    }

    // ========== Tests de eliminar mascota ==========

    @Test
    fun `deletePet removes pet from list`() = runTest {
        val pets = listOf(
            Pet("pet1", "Firulais", "Perro", "Labrador", 5, 15.5, "Macho", "Café", null, userId),
            Pet("pet2", "Michi", "Gato", "Siamés", 3, 4.0, "Hembra", "Blanco", null, userId)
        )
        coEvery { petRepository.getPetsByUser(userId) } returns pets
        viewModel = PetViewModel(petRepository, userId)
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { petRepository.deletePet("pet1") } returns Result.success(Unit)
        coEvery { petRepository.getPetsByUser(userId) } returns pets.filter { it.id != "pet1" }

        viewModel.deletePet("pet1")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.pets.test {
            val petList = awaitItem()
            assertThat(petList).hasSize(1)
            assertThat(petList[0].id).isEqualTo("pet2")
        }
    }

    @Test
    fun `deletePet failure shows error message`() = runTest {
        createViewModel()
        coEvery { petRepository.deletePet("pet1") } returns Result.failure(Exception("Error al eliminar"))

        viewModel.deletePet("pet1")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.errorMessage).contains("Error al eliminar")
        }
    }

    // ========== Tests de cargar mascotas ==========

    @Test
    fun `refreshPets loads pets from repository`() = runTest {
        val pets = listOf(
            Pet("pet1", "Firulais", "Perro", "Labrador", 5, 15.5, "Macho", "Café", null, userId)
        )
        coEvery { petRepository.getPetsByUser(userId) } returns pets
        viewModel = PetViewModel(petRepository, userId)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.pets.test {
            val petList = awaitItem()
            assertThat(petList).hasSize(1)
            assertThat(petList[0].name).isEqualTo("Firulais")
        }
    }

    // ========== Tests de estado ==========

    @Test
    fun `resetSuccessState resets isSuccess to false`() = runTest {
        createViewModel()

        viewModel.resetSuccessState()

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.isSuccess).isFalse()
        }
    }

    @Test
    fun `clearError clears error message`() = runTest {
        createViewModel()

        viewModel.clearError()

        viewModel.formState.test {
            val state = awaitItem()
            assertThat(state.errorMessage).isNull()
        }
    }
}
