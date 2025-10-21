package com.example.pethome.repository

import com.example.pethome.data.SessionManager
import kotlinx.coroutines.delay

data class User(
    val id: String,
    val email: String,
    val name: String
)

class AuthRepository(
    private val sessionManager: SessionManager
) {
    private val registeredUsers = mutableListOf(
        User(id = "1", email = "carla.rojas@gmail.com", name = "Carla Rojas") to "123456",
        User(id = "2", email = "test@test.com", name = "test@test.com") to "password"
    )

    suspend fun login(email: String, password: String): Result<User> {
        delay(1000)

        val userPair = registeredUsers.find { it.first.email == email }

        return if (userPair != null && userPair.second == password) {
            // Guardar sesión en DataStore
            sessionManager.saveUserSession(
                userId = userPair.first.id,
                email = userPair.first.email,
                name = userPair.first.name
            )
            Result.success(userPair.first)
        } else {
            Result.failure(Exception("Correo o contraseña incorrectos"))
        }
    }

    suspend fun register(email: String, password: String, name: String): Result<User> {
        // Simular llamada de red
        delay(1000)

        // Verificar si el usuario ya existe
        if (registeredUsers.any { it.first.email == email }) {
            return Result.failure(Exception("El correo ya está registrado"))
        }

        val newUser = User(
            id = (registeredUsers.size + 1).toString(),
            email = email,
            name = name
        )

        registeredUsers.add(newUser to password)

        // Guardar sesión automáticamente después del registro
        sessionManager.saveUserSession(
            userId = newUser.id,
            email = newUser.email,
            name = newUser.name
        )

        return Result.success(newUser)
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        // Simular llamada de red
        delay(1000)

        val userExists = registeredUsers.any { it.first.email == email }

        return if (userExists) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("El correo no está registrado"))
        }
    }
}