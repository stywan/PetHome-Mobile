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

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(
            name = name,
            nameError = null
        )}
    }

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

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = null
        )}
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(
            isPasswordVisible = !it.isPasswordVisible
        )}
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(
            isConfirmPasswordVisible = !it.isConfirmPasswordVisible
        )}
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun register() {
        if (!validateFields()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val result = authRepository.register(
                    email = _uiState.value.email,
                    password = _uiState.value.password,
                    name = _uiState.value.name
                )

                result.fold(
                    onSuccess = { user ->
                        _uiState.update { it.copy(
                            isLoading = false,
                            isRegisterSuccessful = true,
                            errorMessage = null
                        )}
                    },
                    onFailure = { exception ->
                        _uiState.update { it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Error al registrar usuario"
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

        // Validar nombre
        if (_uiState.value.name.isBlank()) {
            _uiState.update { it.copy(nameError = "El nombre es requerido") }
            isValid = false
        } else if (_uiState.value.name.length < 2) {
            _uiState.update { it.copy(nameError = "El nombre debe tener al menos 2 caracteres") }
            isValid = false
        }

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

        // Validar confirmación de contraseña
        if (_uiState.value.confirmPassword.isBlank()) {
            _uiState.update { it.copy(confirmPasswordError = "Confirma tu contraseña") }
            isValid = false
        } else if (_uiState.value.password != _uiState.value.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Las contraseñas no coinciden") }
            isValid = false
        }

        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }
}

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isRegisterSuccessful: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val errorMessage: String? = null
)

// Factory para crear el ViewModel con el repositorio
class RegisterViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
