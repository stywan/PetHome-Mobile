package com.example.pethome.data.dao

import androidx.room.*
import com.example.pethome.data.model.Pet
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {

    @Query("SELECT * FROM pets WHERE userId = :userId ORDER BY name ASC")
    fun getPetsByUser(userId: String): Flow<List<Pet>>

    @Query("SELECT * FROM pets WHERE id = :petId")
    suspend fun getPetById(petId: String): Pet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: Pet)

    @Update
    suspend fun updatePet(pet: Pet)

    @Delete
    suspend fun deletePet(pet: Pet)

    @Query("DELETE FROM pets WHERE id = :petId")
    suspend fun deletePetById(petId: String)

    @Query("SELECT * FROM pets")
    fun getAllPets(): Flow<List<Pet>>
}
