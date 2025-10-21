package com.example.pethome.data.dao

import androidx.room.*
import com.example.pethome.data.model.Appointment
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {

    @Query("SELECT * FROM appointments WHERE userId = :userId ORDER BY date DESC")
    fun getAppointmentsByUser(userId: String): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE id = :appointmentId")
    suspend fun getAppointmentById(appointmentId: String): Appointment?

    @Query("SELECT * FROM appointments WHERE petId = :petId ORDER BY date DESC")
    fun getAppointmentsByPet(petId: String): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE serviceId = :serviceId ORDER BY date DESC")
    fun getAppointmentsByService(serviceId: String): Flow<List<Appointment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment)

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)

    @Query("DELETE FROM appointments WHERE id = :appointmentId")
    suspend fun deleteAppointmentById(appointmentId: String)
}
