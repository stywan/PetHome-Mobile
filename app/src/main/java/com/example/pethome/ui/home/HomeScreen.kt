package com.example.pethome.ui.home


// Importar los prorios componentes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.pethome.ui.home.components.BannerCard
import com.example.pethome.ui.home.components.BottomNavBar
import com.example.pethome.ui.home.components.HomeTopBar
import com.example.pethome.ui.home.components.MedicineSchedule
import com.example.pethome.ui.home.components.QuickAccessRow


@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit = {}
) {
    Scaffold(
        topBar = { HomeTopBar() },
        bottomBar = { BottomNavBar(onNavigate) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item { BannerCard() }
            item { QuickAccessRow() }
            item { MedicineSchedule() }
            // Aquí puedes seguir agregando secciones
        }
    }
}
