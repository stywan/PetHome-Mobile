package com.example.pethome.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.pethome.repository.AuthRepository
import com.example.pethome.repository.User
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        viewModel = RegisterViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========== Tests de cambio de estado ==========

    @Test
    fun `onNameChange updates name and clears error`() = runTest {
        val name = "Juan Pérez"

        viewModel.onNameChange(name)

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.name).isEqualTo(name)
            assertThat(state.nameError).isNull()
        }
    }

    @Test
    fun `onEmailChange updates email and clears error`() = runTest {
        val email = "test@example.com"

        viewModel.onEmailChange(email)

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.email).isEqualTo(email)
            assertThat(state.emailError).isNull()
        }
    }

    @Test
    fun `onPasswordChange updates password and clears error`() = runTest {
        val password = "password123"

        viewModel.onPasswordChange(password)

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.password).isEqualTo(password)
            assertThat(state.passwordError).isNull()
        }
    }

    @Test
    fun `onConfirmPasswordChange updates confirmPassword and clears error`() = runTest {
        val confirmPassword = "password123"

        viewModel.onConfirmPasswordChange(confirmPassword)

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.confirmPassword).isEqualTo(confirmPassword)
            assertThat(state.confirmPasswordError).isNull()
        }
    }

    @Test
    fun `togglePasswordVisibility toggles password visibility`() = runTest {
        viewModel.togglePasswordVisibility()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isPasswordVisible).isTrue()
        }

        viewModel.togglePasswordVisibility()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isPasswordVisible).isFalse()
        }
    }

    @Test
    fun `toggleConfirmPasswordVisibility toggles confirm password visibility`() = runTest {
        viewModel.toggleConfirmPasswordVisibility()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isConfirmPasswordVisible).isTrue()
        }
    }

    // ========== Tests de validación - Nombre ==========

    @Test
    fun `register with empty name shows error`() = runTest {
        viewModel.onNameChange("")
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")

        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.nameError).isEqualTo("El nombre es requerido")
        }
    }

    @Test
    fun `register with short name shows error`() = runTest {
        viewModel.onNameChange("A")
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")

        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.nameError).isEqualTo("El nombre debe tener al menos 2 caracteres")
        }
    }

    // ========== Tests de validación - Email ==========

    @Test
    fun `register with empty email shows error`() = runTest {
        viewModel.onNameChange("Juan Pérez")
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")

        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.emailError).isEqualTo("El correo es requerido")
        }
    }

    @Test
    fun `register with invalid email shows error`() = runTest {
        viewModel.onNameChange("Juan Pérez")
        viewModel.onEmailChange("invalid-email")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")

        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.emailError).isEqualTo("Correo inválido")
        }
    }

    // ========== Tests de validación - Password ==========

    @Test
    fun `register with empty password shows error`() = runTest {
        viewModel.onNameChange("Juan Pérez")
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("")
        viewModel.onConfirmPasswordChange("")

        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.passwordError).isEqualTo("La contraseña es requerida")
        }
    }

    @Test
    fun `register with short password shows error`() = runTest {
        viewModel.onNameChange("Juan Pérez")
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("12345")
        viewModel.onConfirmPasswordChange("12345")

        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.passwordError).isEqualTo("La contraseña debe tener al menos 6 caracteres")
        }
    }

    // ========== Tests de validación - Confirm Password ==========

    @Test
    fun `register with empty confirm password shows error`() = runTest {
        viewModel.onNameChange("Juan Pérez")
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("")

        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.confirmPasswordError).isEqualTo("Confirma tu contraseña")
        }
    }

    @Test
    fun `register with mismatched passwords shows error`() = runTest {
        viewModel.onNameChange("Juan Pérez")
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("differentpassword")

        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.confirmPasswordError).isEqualTo("Las contraseñas no coinciden")
        }
    }

    // ========== Tests de registro exitoso ==========

    @Test
    fun `register with valid data succeeds`() = runTest {
        val name = "Juan Pérez"
        val email = "test@example.com"
        val password = "password123"
        val user = User(id = "1", email = email, name = name)

        viewModel.onNameChange(name)
        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)
        viewModel.onConfirmPasswordChange(password)

        coEvery { authRepository.register(email, password, name) } returns Result.success(user)

        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isRegisterSuccessful).isTrue()
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorMessage).isNull()
        }
    }

    @Test
    fun `register with existing email shows error`() = runTest {
        val name = "Juan Pérez"
        val email = "existing@example.com"
        val password = "password123"

        viewModel.onNameChange(name)
        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)
        viewModel.onConfirmPasswordChange(password)

        coEvery { authRepository.register(email, password, name) } returns
            Result.failure(Exception("El correo ya está registrado"))

        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isRegisterSuccessful).isFalse()
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorMessage).contains("El correo ya está registrado")
        }
    }

    // ========== Tests de estado inicial ==========

    @Test
    fun `initial state has default values`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.name).isEmpty()
            assertThat(state.email).isEmpty()
            assertThat(state.password).isEmpty()
            assertThat(state.confirmPassword).isEmpty()
            assertThat(state.isPasswordVisible).isFalse()
            assertThat(state.isConfirmPasswordVisible).isFalse()
            assertThat(state.isLoading).isFalse()
            assertThat(state.isRegisterSuccessful).isFalse()
            assertThat(state.nameError).isNull()
            assertThat(state.emailError).isNull()
            assertThat(state.passwordError).isNull()
            assertThat(state.confirmPasswordError).isNull()
            assertThat(state.errorMessage).isNull()
        }
    }

    @Test
    fun `clearError clears error message`() = runTest {
        viewModel.clearError()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.errorMessage).isNull()
        }
    }
}
