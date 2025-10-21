package com.example.pethome.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val serviceId: String,
    val petId: String,
    val userId: String,
    val date: Long, // Timestamp
    val time: String, // Ej: "10:00 AM"
    val status: String, // Pendiente, Confirmada, Cancelada, Completada
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
