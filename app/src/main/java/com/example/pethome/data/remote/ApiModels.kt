package com.example.pethome.data.remote

import com.google.gson.annotations.SerializedName

// ============ AUTH MODELS ============

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val type: String,
    val id: String,
    val email: String,
    val name: String,
    val role: String
)

// ============ PET MODELS ============

data class PetRequest(
    val name: String,
    val species: String,
    val breed: String,
    val age: Int,
    val weight: Double,
    val gender: String,
    val color: String,
    val imageUrl: String? = null
)

data class PetResponse(
    val id: String,
    val name: String,
    val species: String,
    val breed: String,
    val age: Int,
    val weight: Double,
    val gender: String,
    val color: String,
    val imageUrl: String?,
    val userId: String,
    val createdAt: String,
    val updatedAt: String
)

// ============ ERROR RESPONSE ============

data class ErrorResponse(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val validationErrors: Map<String, String>? = null
)
