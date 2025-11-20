package com.example.pethome.repository

import android.util.Log
import com.example.pethome.data.SessionManager
import com.example.pethome.data.remote.LoginRequest
import com.example.pethome.data.remote.RegisterRequest
import com.example.pethome.data.remote.RetrofitClient

data class User(
    val id: String,
    val email: String,
    val name: String
)

class AuthRepository(
    private val sessionManager: SessionManager
) {

    private val TAG = "AuthRepository"

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            Log.d(TAG, "Attempting login for: $email")

            val response = RetrofitClient.authApi.login(
                LoginRequest(email, password)
            )

            Log.d(TAG, "Login successful for: ${response.email}")

            // Guardar token y datos de usuario en DataStore
            sessionManager.saveUserSession(
                userId = response.id,
                email = response.email,
                name = response.name,
                token = response.token
            )

            val user = User(
                id = response.id,
                email = response.email,
                name = response.name
            )

            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed", e)
            Result.failure(Exception("Correo o contraseña incorrectos. Verifica tu conexión."))
        }
    }

    suspend fun register(email: String, password: String, name: String): Result<User> {
        return try {
            Log.d(TAG, "Attempting registration for: $email")

            val response = RetrofitClient.authApi.register(
                RegisterRequest(name, email, password)
            )

            Log.d(TAG, "Registration successful for: ${response.email}")

            // Guardar token y datos de usuario en DataStore
            sessionManager.saveUserSession(
                userId = response.id,
                email = response.email,
                name = response.name,
                token = response.token
            )

            val user = User(
                id = response.id,
                email = response.email,
                name = response.name
            )

            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed", e)
            Result.failure(Exception("Error al registrar. El correo puede estar ya registrado."))
        }
    }

    suspend fun logout() {
        Log.d(TAG, "Logging out user")
        sessionManager.clearSession()
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        // TODO: Implementar cuando el backend tenga endpoint de reset password
        Log.d(TAG, "Reset password for: $email")
        return Result.failure(Exception("Funcionalidad no disponible aún"))
    }
}