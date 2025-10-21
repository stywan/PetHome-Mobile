package com.example.pethome.data.dao

import androidx.room.*
import com.example.pethome.data.model.VeterinaryService
import kotlinx.coroutines.flow.Flow

@Dao
interface VeterinaryServiceDao {

    @Query("SELECT * FROM veterinary_services WHERE isAvailable = 1 ORDER BY category ASC")
    fun getAllServices(): Flow<List<VeterinaryService>>

    @Query("SELECT * FROM veterinary_services WHERE id = :serviceId")
    suspend fun getServiceById(serviceId: String): VeterinaryService?

    @Query("SELECT * FROM veterinary_services WHERE category = :category AND isAvailable = 1")
    fun getServicesByCategory(category: String): Flow<List<VeterinaryService>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: VeterinaryService)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<VeterinaryService>)

    @Update
    suspend fun updateService(service: VeterinaryService)

    @Delete
    suspend fun deleteService(service: VeterinaryService)
}
