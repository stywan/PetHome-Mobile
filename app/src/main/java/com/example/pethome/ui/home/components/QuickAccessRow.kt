package com.example.pethome.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuickAccessRow(onNavigate: (String) -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickAccessCard(
            title = "Mis Mascotas",
            buttonText = "Ver todas",
            modifier = Modifier.weight(1f),
            onClick = { onNavigate("pet_list") }
        )
        QuickAccessCard(
            title = "Servicios veterinarios",
            buttonText = "Ver más",
            modifier = Modifier.weight(1f),
            onClick = { onNavigate("service_list") }
        )
    }
}
