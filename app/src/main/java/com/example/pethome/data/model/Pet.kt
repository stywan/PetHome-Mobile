package com.example.pethome.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val species: String, // Perro, Gato, etc.
    val breed: String, // Raza
    val age: Int,
    val weight: Double, // en kg
    val gender: String, // Macho, Hembra
    val color: String,
    val imageUrl: String? = null,
    val userId: String // ID del usuario dueño
)
