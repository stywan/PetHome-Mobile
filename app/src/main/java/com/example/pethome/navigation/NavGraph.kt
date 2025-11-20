package com.example.pethome.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pethome.data.SessionManager
import com.example.pethome.data.database.AppDatabase
import com.example.pethome.repository.AuthRepository
import com.example.pethome.repository.PetRepository
import com.example.pethome.ui.home.HomeScreen
import com.example.pethome.ui.login.LoginScreen
import com.example.pethome.ui.pets.AddEditPetScreen
import com.example.pethome.ui.pets.PetListScreen
import com.example.pethome.ui.register.RegisterScreen
import com.example.pethome.ui.services.ServiceDetailScreen
import com.example.pethome.ui.services.ServiceListScreen
import com.example.pethome.viewmodel.LoginViewModel
import com.example.pethome.viewmodel.LoginViewModelFactory
import com.example.pethome.viewmodel.RegisterViewModel
import com.example.pethome.viewmodel.RegisterViewModelFactory
import com.example.pethome.viewmodel.PetViewModel
import com.example.pethome.viewmodel.PetViewModelFactory
import com.example.pethome.viewmodel.ServiceViewModel
import com.example.pethome.viewmodel.ServiceViewModelFactory
import com.example.pethome.viewmodel.ScheduleViewModel
import com.example.pethome.viewmodel.ScheduleViewModelFactory
import com.example.pethome.repository.ServiceRepository


// Rutas de navegación
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object PetList : Screen("pet_list")
    object AddPet : Screen("add_pet")
    object EditPet : Screen("edit_pet")
    object ServiceList : Screen("service_list")
    object ServiceDetail : Screen("service_detail/{serviceId}") {
        fun createRoute(serviceId: String) = "service_detail/$serviceId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val authRepository = AuthRepository(sessionManager)

    // Inicializar la base de datos y los repositorios
    val database = remember { AppDatabase.getDatabase(context) }
    val petRepository = remember { PetRepository(database.petDao()) }
    val serviceRepository = remember { ServiceRepository(database.veterinaryServiceDao(), database.appointmentDao()) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla de Login
        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(authRepository)
            )

            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    // TODO: Implementar cuando tengamos ForgotPasswordScreen
                },
                onLoginSuccess = {
                    // Navegar al Home y limpiar el stack
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Registro
        composable(Screen.Register.route) {
            val viewModel: RegisterViewModel = viewModel(
                factory = RegisterViewModelFactory(authRepository)
            )

            RegisterScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.navigateUp()
                },
                onRegisterSuccess = {
                    // Navegar al Home y limpiar el stack (el usuario ya está logueado)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Home
        composable(Screen.Home.route) { backStackEntry ->
            val userId by sessionManager.userId.collectAsState(initial = "")

            // Compartir ViewModel a nivel de NavGraph
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }
            val scheduleViewModel: ScheduleViewModel = viewModel(
                viewModelStoreOwner = parentEntry,
                factory = ScheduleViewModelFactory(petRepository, serviceRepository, userId ?: "")
            )

            HomeScreen(
                scheduleViewModel = scheduleViewModel,
                onNavigate = { route ->
                    navController.navigate(route)
                },
                onLogout = {
                    // Navegar al Login y limpiar el stack
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Lista de Mascotas
        composable(Screen.PetList.route) { backStackEntry ->
            val userId by sessionManager.userId.collectAsState(initial = "")

            // Compartir ViewModel a nivel de NavGraph
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }
            val petViewModel: PetViewModel = viewModel(
                viewModelStoreOwner = parentEntry,
                factory = PetViewModelFactory(petRepository, userId ?: "")
            )

            PetListScreen(
                viewModel = petViewModel,
                onNavigateBack = { navController.navigateUp() },
                onAddPet = {
                    petViewModel.clearForm()
                    navController.navigate(Screen.AddPet.route)
                },
                onEditPet = { pet ->
                    petViewModel.startEditingPet(pet)
                    navController.navigate(Screen.EditPet.route)
                }
            )
        }

        // Pantalla de Agregar Mascota
        composable(Screen.AddPet.route) { backStackEntry ->
            val userId by sessionManager.userId.collectAsState(initial = "")

            // Usar el mismo ViewModel compartido
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }
            val petViewModel: PetViewModel = viewModel(
                viewModelStoreOwner = parentEntry,
                factory = PetViewModelFactory(petRepository, userId ?: "")
            )

            AddEditPetScreen(
                viewModel = petViewModel,
                onNavigateBack = {
                    navController.popBackStack(Screen.PetList.route, inclusive = false)
                },
                isEditing = false
            )
        }

        // Pantalla de Editar Mascota
        composable(Screen.EditPet.route) { backStackEntry ->
            val userId by sessionManager.userId.collectAsState(initial = "")

            // Usar el mismo ViewModel compartido
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }
            val petViewModel: PetViewModel = viewModel(
                viewModelStoreOwner = parentEntry,
                factory = PetViewModelFactory(petRepository, userId ?: "")
            )

            AddEditPetScreen(
                viewModel = petViewModel,
                onNavigateBack = {
                    navController.popBackStack(Screen.PetList.route, inclusive = false)
                },
                isEditing = true
            )
        }

        // Pantalla de Lista de Servicios
        composable(Screen.ServiceList.route) { backStackEntry ->
            val userId by sessionManager.userId.collectAsState(initial = "")

            // Compartir ViewModel a nivel de NavGraph
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }
            val serviceViewModel: ServiceViewModel = viewModel(
                viewModelStoreOwner = parentEntry,
                factory = ServiceViewModelFactory(serviceRepository, userId ?: "")
            )

            ServiceListScreen(
                viewModel = serviceViewModel,
                onNavigateBack = { navController.navigateUp() },
                onServiceClick = { serviceId ->
                    navController.navigate(Screen.ServiceDetail.createRoute(serviceId))
                }
            )
        }

        // Pantalla de Detalle de Servicio
        composable(Screen.ServiceDetail.route) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            val userId by sessionManager.userId.collectAsState(initial = "")

            // Usar el mismo ViewModel compartido para servicios
            val serviceParentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }
            val serviceViewModel: ServiceViewModel = viewModel(
                viewModelStoreOwner = serviceParentEntry,
                factory = ServiceViewModelFactory(serviceRepository, userId ?: "")
            )

            // Usar el mismo ViewModel compartido para mascotas
            val petParentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(navController.graph.id)
            }
            val petViewModel: PetViewModel = viewModel(
                viewModelStoreOwner = petParentEntry,
                factory = PetViewModelFactory(petRepository, userId ?: "")
            )

            ServiceDetailScreen(
                serviceId = serviceId,
                serviceViewModel = serviceViewModel,
                petViewModel = petViewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}
