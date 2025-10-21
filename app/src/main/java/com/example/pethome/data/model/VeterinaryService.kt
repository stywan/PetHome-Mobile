package com.example.pethome.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "veterinary_services")
data class VeterinaryService(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val description: String,
    val shortDescription: String,
    val price: Double,
    val duration: Int, // en minutos
    val category: String, // Consulta, Vacunación, Cirugía, etc.
    val imageUrl: String? = null,
    val isAvailable: Boolean = true
)
