package com.example.pethome.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class Medicine(
    val petName: String,
    val medicine: String,
    val time: String,
    val dose: String
)

val sampleMedicines = listOf(
    Medicine("Pelusa", "Antibiótico - Veraflox", "11:30 am", "3 mg"),
    Medicine("Luke", "Vitaminas - Apetipet", "15:00 pm", "1 píldora")
)

@Composable
fun MedicineSchedule() {
    Column(Modifier.padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Horario Medicina",
                style = MaterialTheme.typography.titleLarge // Cambiado h6 → titleLarge
            )
            Icon(Icons.Default.MoreVert, contentDescription = null)
        }

        Spacer(Modifier.height(8.dp))
        sampleMedicines.forEach { medicine ->
            MedicineItem(medicine)
        }
    }
}

@Composable
fun MedicineItem(medicine: Medicine) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAFBF8)), // reemplaza backgroundColor
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // reemplaza elevation
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFF7A5DE8))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(medicine.petName, color = Color(0xFF7A5DE8))
                Text(medicine.medicine, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text(medicine.time, style = MaterialTheme.typography.bodyMedium)
                Text(medicine.dose, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

}
