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
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        viewModel = LoginViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========== Tests de cambio de estado ==========

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
    fun `togglePasswordVisibility toggles visibility state`() = runTest {
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
    fun `clearError clears error message`() = runTest {
        viewModel.clearError()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.errorMessage).isNull()
        }
    }

    // ========== Tests de validación ==========

    @Test
    fun `login with empty email shows error`() = runTest {
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("password123")

        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.emailError).isEqualTo("El correo es requerido")
        }
    }

    @Test
    fun `login with invalid email shows error`() = runTest {
        viewModel.onEmailChange("invalid-email")
        viewModel.onPasswordChange("password123")

        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.emailError).isEqualTo("Correo inválido")
        }
    }

    @Test
    fun `login with empty password shows error`() = runTest {
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("")

        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.passwordError).isEqualTo("La contraseña es requerida")
        }
    }

    @Test
    fun `login with short password shows error`() = runTest {
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("12345")

        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.passwordError).isEqualTo("La contraseña debe tener al menos 6 caracteres")
        }
    }

    // ========== Tests de login exitoso ==========

    @Test
    fun `login with valid credentials succeeds`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val user = User(id = "1", email = email, name = "Test User")

        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)

        coEvery { authRepository.login(email, password) } returns Result.success(user)

        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoginSuccessful).isTrue()
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorMessage).isNull()
        }
    }

    @Test
    fun `login with invalid credentials shows error`() = runTest {
        val email = "test@example.com"
        val password = "wrongpassword"

        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)

        coEvery { authRepository.login(email, password) } returns
            Result.failure(Exception("Correo o contraseña incorrectos"))

        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoginSuccessful).isFalse()
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorMessage).contains("Correo o contraseña incorrectos")
        }
    }

    // ========== Tests de estado inicial ==========

    @Test
    fun `initial state has default values`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.email).isEmpty()
            assertThat(state.password).isEmpty()
            assertThat(state.isPasswordVisible).isFalse()
            assertThat(state.isLoading).isFalse()
            assertThat(state.isLoginSuccessful).isFalse()
            assertThat(state.emailError).isNull()
            assertThat(state.passwordError).isNull()
            assertThat(state.errorMessage).isNull()
        }
    }
}
