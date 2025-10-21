package com.example.pethome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pethome.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(
            email = email,
            emailError = null
        )}
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(
            password = password,
            passwordError = null
        )}
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(
            isPasswordVisible = !it.isPasswordVisible
        )}
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun login() {
        // Validar campos
        if (!validateFields()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val result = authRepository.login(
                    email = _uiState.value.email,
                    password = _uiState.value.password
                )

                result.fold(
                    onSuccess = { user ->
                        _uiState.update { it.copy(
                            isLoading = false,
                            isLoginSuccessful = true,
                            errorMessage = null
                        )}
                    },
                    onFailure = { exception ->
                        _uiState.update { it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Error al iniciar sesión"
                        )}
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = "Error inesperado: ${e.message}"
                )}
            }
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true

        // Validar email
        if (_uiState.value.email.isBlank()) {
            _uiState.update { it.copy(emailError = "El correo es requerido") }
            isValid = false
        } else if (!isValidEmail(_uiState.value.email)) {
            _uiState.update { it.copy(emailError = "Correo inválido") }
            isValid = false
        }

        // Validar contraseña
        if (_uiState.value.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "La contraseña es requerida") }
            isValid = false
        } else if (_uiState.value.password.length < 6) {
            _uiState.update { it.copy(passwordError = "La contraseña debe tener al menos 6 caracteres") }
            isValid = false
        }

        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val errorMessage: String? = null
)

// Factory para crear el ViewModel con el repositorio
class LoginViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}