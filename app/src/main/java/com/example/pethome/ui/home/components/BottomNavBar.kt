package com.example.pethome.ui.home.components

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color

sealed class BottomNavItem(val route: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", Icons.Default.Home)
    object Calendar : BottomNavItem("calendar", Icons.Default.DateRange)
    object Profile : BottomNavItem("profile", Icons.Default.Person)
    object Pets : BottomNavItem("pets", Icons.Default.Favorite) // Cambié Pets por Favorite
}

@Composable
fun BottomNavBar(onNavigate: (String) -> Unit) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Calendar,
        BottomNavItem.Profile,
        BottomNavItem.Pets
    )

    NavigationBar(
        containerColor = Color.White
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                selected = false,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}
