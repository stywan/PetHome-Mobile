package com.example.pethome.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pethome.R
import com.example.pethome.data.SessionManager
import com.example.pethome.repository.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun HomeTopBar(
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val authRepository = AuthRepository(sessionManager)
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo PetHome
        Image(
            painter = painterResource(id = R.drawable.logo_pethome),
            contentDescription = "PetHome Logo",
            modifier = Modifier.height(40.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { /* TODO: Implementar búsqueda */ }) {
                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.Gray)
            }
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        authRepository.logout()
                        onLogout()
                    }
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Cerrar sesión",
                    tint = Color(0xFFE57373)
                )
            }
        }
    }
}
