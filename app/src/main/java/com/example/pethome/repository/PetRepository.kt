package com.example.pethome.repository

import android.util.Log
import com.example.pethome.data.dao.PetDao
import com.example.pethome.data.model.Pet
import com.example.pethome.data.remote.PetRequest
import com.example.pethome.data.remote.PetResponse
import com.example.pethome.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class PetRepository(private val petDao: PetDao) {

    private val TAG = "PetRepository"

    /**
     * Convierte PetResponse de la API a Pet local
     */
    private fun PetResponse.toPet(userId: String): Pet {
        return Pet(
            id = this.id,
            name = this.name,
            species = this.species,
            breed = this.breed,
            age = this.age,
            weight = this.weight,
            gender = this.gender,
            color = this.color,
            imageUrl = this.imageUrl,
            userId = userId
        )
    }

    /**
     * Convierte Pet local a PetRequest para la API
     */
    private fun Pet.toPetRequest(): PetRequest {
        return PetRequest(
            name = this.name,
            species = this.species,
            breed = this.breed,
            age = this.age,
            weight = this.weight,
            gender = this.gender,
            color = this.color,
            imageUrl = this.imageUrl
        )
    }

    /**
     * Obtener mascotas: SOLO DESDE BACKEND (sin fallback local)
     */
    suspend fun getPetsByUser(userId: String): List<Pet> {
        return try {
            Log.d(TAG, "Fetching pets from API for user: $userId")

            // Obtener SOLO de la API
            val remotePets = RetrofitClient.petApi.getPets()
            val pets = remotePets.map { it.toPet(userId) }

            Log.d(TAG, "✅ Successfully fetched ${pets.size} pets from API")

            pets
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to fetch pets from API", e)

            // En caso de error, lanzar excepción
            throw Exception("No se pudo conectar con el servidor. Verifica tu conexión.", e)
        }
    }

    /**
     * Agregar mascota: SOLO EN BACKEND (sin fallback local)
     */
    suspend fun addPet(pet: Pet): Result<Pet> {
        return try {
            Log.d(TAG, "Creating pet in API: ${pet.name}")

            val request = pet.toPetRequest()
            val response = RetrofitClient.petApi.createPet(request)
            val createdPet = response.toPet(pet.userId)

            Log.d(TAG, "✅ Pet created successfully in API with id: ${createdPet.id}")

            Result.success(createdPet)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to create pet in API", e)

            // NO guardar local, retornar error
            Result.failure(Exception("No se pudo crear la mascota. Verifica tu conexión.", e))
        }
    }

    /**
     * Actualizar mascota: SOLO EN BACKEND (sin fallback local)
     */
    suspend fun updatePet(pet: Pet): Result<Pet> {
        return try {
            Log.d(TAG, "Updating pet in API: ${pet.id}")

            val request = pet.toPetRequest()
            val response = RetrofitClient.petApi.updatePet(pet.id, request)
            val updatedPet = response.toPet(pet.userId)

            Log.d(TAG, "✅ Pet updated successfully in API")

            Result.success(updatedPet)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to update pet in API", e)

            // NO actualizar local, retornar error
            Result.failure(Exception("No se pudo actualizar la mascota. Verifica tu conexión.", e))
        }
    }

    /**
     * Eliminar mascota: SOLO EN BACKEND (sin fallback local)
     */
    suspend fun deletePet(petId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Deleting pet from API: $petId")

            // Eliminar SOLO de API
            RetrofitClient.petApi.deletePet(petId)

            Log.d(TAG, "✅ Pet deleted successfully from API")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to delete pet from API", e)

            // NO eliminar local, retornar error
            Result.failure(Exception("No se pudo eliminar la mascota. Verifica tu conexión.", e))
        }
    }

    /**
     * Obtener mascota por ID: SOLO DESDE BACKEND
     */
    suspend fun getPetById(petId: String): Pet? {
        return try {
            Log.d(TAG, "Fetching pet by id from API: $petId")
            val response = RetrofitClient.petApi.getPetById(petId)

            Log.d(TAG, "✅ Pet fetched successfully from API")

            // El userId viene en la respuesta de la API
            response.toPet(response.userId)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to fetch pet from API", e)
            null
        }
    }
}
