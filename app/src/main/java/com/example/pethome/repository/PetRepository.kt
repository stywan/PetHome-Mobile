package com.example.pethome.repository

import com.example.pethome.data.dao.PetDao
import com.example.pethome.data.model.Pet
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class PetRepository(private val petDao: PetDao) {

    // Obtener mascotas por usuario
    fun getPetsByUser(userId: String): Flow<List<Pet>> {
        return petDao.getPetsByUser(userId)
    }

    // Agregar mascota
    suspend fun addPet(pet: Pet): Result<Pet> {
        return try {
            val newPet = pet.copy(id = UUID.randomUUID().toString())
            petDao.insertPet(newPet)
            Result.success(newPet)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar mascota
    suspend fun updatePet(pet: Pet): Result<Pet> {
        return try {
            petDao.updatePet(pet)
            Result.success(pet)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Eliminar mascota
    suspend fun deletePet(petId: String): Result<Unit> {
        return try {
            petDao.deletePetById(petId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener mascota por ID
    suspend fun getPetById(petId: String): Pet? {
        return petDao.getPetById(petId)
    }
}
