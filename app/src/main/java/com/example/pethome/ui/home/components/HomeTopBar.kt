package com.example.pethome.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFF7A5DE8))
            Spacer(Modifier.width(8.dp))
            Text(
                text = "PetHome",
                style = MaterialTheme.typography.titleLarge, // Cambié h6 por titleLarge
                color = Color(0xFF7A5DE8)
            )
        }
        Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.Gray)
    }
}
