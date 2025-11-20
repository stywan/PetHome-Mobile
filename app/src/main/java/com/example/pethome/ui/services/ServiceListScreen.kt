package com.example.pethome.ui.services

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethome.R
import com.example.pethome.data.model.VeterinaryService
import com.example.pethome.viewmodel.ServiceViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceListScreen(
    viewModel: ServiceViewModel,
    onNavigateBack: () -> Unit,
    onServiceClick: (String) -> Unit
) {
    val services by viewModel.services.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Servicios Veterinarios", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF7A5DE8),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (services.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF7A5DE8))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Explora nuestros servicios",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D2D)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Selecciona el servicio que necesitas para tu mascota",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                items(services) { service ->
                    ServiceCard(
                        service = service,
                        onClick = { onServiceClick(service.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun ServiceCard(
    service: VeterinaryService,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column {
            // Imagen del servicio
            Image(
                painter = painterResource(id = getServiceImage(service.category)),
                contentDescription = service.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Categoría
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = getCategoryColor(service.category).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = service.category,
                                fontSize = 12.sp,
                                color = getCategoryColor(service.category),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Nombre del servicio
                        Text(
                            text = service.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D2D)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Descripción corta
                        Text(
                            text = service.shortDescription,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Precio y duración
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${service.duration} min",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatPrice(service.price),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7A5DE8)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF7A5DE8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

fun getCategoryIcon(category: String) = when (category) {
    "Consulta" -> Icons.Default.MedicalServices
    "Prevención" -> Icons.Default.Shield
    "Estética" -> Icons.Default.Spa
    "Cirugía" -> Icons.Default.LocalHospital
    "Especializada" -> Icons.Default.Star
    "Diagnóstico" -> Icons.Default.Science
    "Urgencia" -> Icons.Default.Emergency
    else -> Icons.Default.Pets
}

fun getCategoryColor(category: String) = when (category) {
    "Consulta" -> Color(0xFF2196F3)
    "Prevención" -> Color(0xFF4CAF50)
    "Estética" -> Color(0xFFE91E63)
    "Cirugía" -> Color(0xFFF44336)
    "Especializada" -> Color(0xFFFF9800)
    "Diagnóstico" -> Color(0xFF9C27B0)
    "Urgencia" -> Color(0xFFFF5252)
    else -> Color(0xFF7A5DE8)
}

fun formatPrice(price: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    return format.format(price)
}

fun getServiceImage(category: String): Int = when (category) {
    "Consulta" -> R.drawable.servicio_general
    "Prevención" -> R.drawable.servicio_general
    "Estética" -> R.drawable.servicio_limpieza
    "Cirugía" -> R.drawable.servicio_cirugia
    "Especializada" -> R.drawable.servicio_general
    "Diagnóstico" -> R.drawable.servicio_examenes
    "Urgencia" -> R.drawable.servicio_cirugia
    else -> R.drawable.servicio_general
}
