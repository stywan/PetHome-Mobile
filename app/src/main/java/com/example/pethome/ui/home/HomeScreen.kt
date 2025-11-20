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
import com.example.pethome.ui.home.components.ScheduleSection
import com.example.pethome.ui.home.components.QuickAccessRow
import com.example.pethome.viewmodel.ScheduleViewModel


@Composable
fun HomeScreen(
    scheduleViewModel: ScheduleViewModel,
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Scaffold(
        topBar = { HomeTopBar(onLogout = onLogout) },
        bottomBar = { BottomNavBar(onNavigate) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item { BannerCard() }
            item { QuickAccessRow(onNavigate = onNavigate) }
            item { ScheduleSection(viewModel = scheduleViewModel) }
        }
    }
}
