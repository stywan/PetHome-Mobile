package com.example.pethome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.pethome.data.SessionManager
import com.example.pethome.data.remote.RetrofitClient
import com.example.pethome.navigation.NavGraph
import com.example.pethome.navigation.Screen
import com.example.pethome.ui.theme.PetHomeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // IMPORTANTE: Inicializar Retrofit con SessionManager
        val sessionManager = SessionManager(applicationContext)
        RetrofitClient.initialize(sessionManager)

        enableEdgeToEdge()
        setContent {
            PetHomeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    // Observar el estado de login
    val isLoggedIn by sessionManager.isLoggedIn.collectAsState(initial = null)

    // Estado para controlar la carga
    var isCheckingSession by remember { mutableStateOf(true) }

    // Determinar la pantalla inicial
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn != null) {
            isCheckingSession = false
        }
    }

    when {
        isCheckingSession -> {
            // Mostrar pantalla de carga mientras verificamos la sesión
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else -> {
            // Determinar la ruta inicial basada en el estado de login
            val startDestination = if (isLoggedIn == true) {
                Screen.Home.route
            } else {
                Screen.Login.route
            }

            NavGraph(
                navController = navController,
                startDestination = startDestination
            )
        }
    }
}

