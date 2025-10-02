package com.example.pethome.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuickAccessRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickAccessCard(
            title = "Visita veterinaria a domicilio",
            buttonText = "Agenda aquí",
            modifier = Modifier.weight(1f)
        )
        QuickAccessCard(
            title = "Servicios veterinarios",
            buttonText = "Ver más",
            modifier = Modifier.weight(1f)
        )
    }
}
