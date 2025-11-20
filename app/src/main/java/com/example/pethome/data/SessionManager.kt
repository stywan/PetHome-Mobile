package com.example.pethome.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión para crear el DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class SessionManager(private val context: Context) {

    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    // Guardar token JWT
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    // Obtener token JWT
    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN_KEY]
    }

    // Guardar sesión del usuario (incluyendo token si se pasa)
    suspend fun saveUserSession(userId: String, email: String, name: String, token: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_NAME_KEY] = name
            if (token != null) {
                preferences[AUTH_TOKEN_KEY] = token
            }
        }
    }

    // Obtener ID del usuario
    val userId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    // Obtener email del usuario
    val userEmail: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL_KEY]
    }

    // Obtener nombre del usuario
    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY]
    }

    // Verificar si hay sesión activa (con token)
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY] != null && preferences[AUTH_TOKEN_KEY] != null
    }

    // Cerrar sesión
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
