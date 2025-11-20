package com.example.pethome.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Event
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.pethome.data.model.ScheduleItem
import com.example.pethome.viewmodel.ScheduleViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScheduleSection(
    viewModel: ScheduleViewModel
) {
    val upcomingItems by viewModel.upcomingScheduleItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(Modifier.padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Próximas Actividades",
                style = MaterialTheme.typography.titleLarge
            )
            Icon(Icons.Default.MoreVert, contentDescription = null)
        }

        Spacer(Modifier.height(8.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (upcomingItems.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay actividades programadas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            upcomingItems.forEach { item ->
                when (item) {
                    is ScheduleItem.MedicineSchedule -> MedicineItem(item)
                    is ScheduleItem.ServiceAppointment -> AppointmentItem(item, viewModel)
                }
            }
        }
    }
}

@Composable
fun MedicineItem(medicine: ScheduleItem.MedicineSchedule) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Medication, contentDescription = null, tint = Color(0xFF7A5DE8))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(medicine.petName, color = Color(0xFF7A5DE8), style = MaterialTheme.typography.titleSmall)
                Text(medicine.medicineName, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text(medicine.time, style = MaterialTheme.typography.bodyMedium)
                Text(medicine.dosage, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun AppointmentItem(appointment: ScheduleItem.ServiceAppointment, viewModel: ScheduleViewModel) {
    val categoryColor = getCategoryColor(appointment.serviceCategory)
    val categoryIcon = getCategoryIcon(appointment.serviceCategory)
    val statusColor = getStatusColor(appointment.status)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    categoryIcon,
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        appointment.petName,
                        color = categoryColor,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        appointment.serviceName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (appointment.serviceDescription.isNotEmpty()) {
                        Text(
                            appointment.serviceDescription,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        viewModel.formatDate(appointment.dateTime),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        appointment.time,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = appointment.status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor
                    )
                }

                Text(
                    text = appointment.serviceCategory,
                    style = MaterialTheme.typography.labelSmall,
                    color = categoryColor
                )
            }
        }
    }
}

private fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "consulta" -> Color(0xFF4CAF50)
        "vacunación", "vacunacion" -> Color(0xFF2196F3)
        "cirugía", "cirugia" -> Color(0xFFFF5722)
        "peluquería", "peluqueria" -> Color(0xFF9C27B0)
        "baño", "bano" -> Color(0xFF00BCD4)
        "veterinaria" -> Color(0xFFFF9800)
        else -> Color(0xFF7A5DE8)
    }
}

private fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "consulta" -> Icons.Default.MedicalServices
        "vacunación", "vacunacion" -> Icons.Default.Medication
        "cirugía", "cirugia" -> Icons.Default.MedicalServices
        "peluquería", "peluqueria", "baño", "bano" -> Icons.Default.Event
        else -> Icons.Default.MedicalServices
    }
}

private fun getStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "pendiente" -> Color(0xFFFF9800)
        "confirmada" -> Color(0xFF4CAF50)
        "cancelada" -> Color(0xFFF44336)
        "completada" -> Color(0xFF2196F3)
        else -> Color.Gray
    }
}
