package com.example.pethome.ui.services

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethome.data.model.Pet
import com.example.pethome.viewmodel.PetViewModel
import com.example.pethome.viewmodel.ServiceViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    serviceId: String,
    serviceViewModel: ServiceViewModel,
    petViewModel: PetViewModel,
    onNavigateBack: () -> Unit
) {
    val service by serviceViewModel.selectedService.collectAsState()
    val pets by petViewModel.pets.collectAsState()
    val formState by serviceViewModel.appointmentFormState.collectAsState()

    var showReservationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(serviceId) {
        serviceViewModel.selectService(serviceId)
    }

    LaunchedEffect(formState.isSuccess) {
        if (formState.isSuccess) {
            serviceViewModel.clearAppointmentForm()
            serviceViewModel.resetSuccessState()
            showReservationDialog = false
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Servicio", fontWeight = FontWeight.Bold) },
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
        if (service == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF7A5DE8))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Icono grande
                        Surface(
                            modifier = Modifier.size(100.dp),
                            shape = RoundedCornerShape(50.dp),
                            color = getCategoryColor(service!!.category).copy(alpha = 0.15f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    getCategoryIcon(service!!.category),
                                    contentDescription = null,
                                    tint = getCategoryColor(service!!.category),
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Categoría
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = getCategoryColor(service!!.category).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = service!!.category,
                                fontSize = 13.sp,
                                color = getCategoryColor(service!!.category),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Nombre
                        Text(
                            text = service!!.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D2D)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Precio
                        Text(
                            text = formatPrice(service!!.price),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7A5DE8)
                        )
                    }
                }

                // Información del servicio
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Descripción",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D2D)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = service!!.description,
                            fontSize = 15.sp,
                            color = Color(0xFF666666),
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            InfoChip(
                                icon = Icons.Default.Schedule,
                                text = "${service!!.duration} min",
                                label = "Duración"
                            )
                            InfoChip(
                                icon = Icons.Default.CheckCircle,
                                text = if (service!!.isAvailable) "Disponible" else "No disponible",
                                label = "Estado"
                            )
                        }
                    }
                }

                // Mensaje de error
                if (formState.errorMessage != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = Color(0xFFD32F2F)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = formState.errorMessage!!,
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón reservar
                Button(
                    onClick = { showReservationDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A5DE8)),
                    enabled = service!!.isAvailable && pets.isNotEmpty()
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (pets.isEmpty()) "Agrega una mascota primero" else "Reservar Cita",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Diálogo de reserva
    if (showReservationDialog && service != null) {
        ReservationDialog(
            service = service!!,
            pets = pets,
            formState = formState,
            onDismiss = {
                showReservationDialog = false
                serviceViewModel.clearAppointmentForm()
            },
            onPetSelected = { serviceViewModel.onPetSelected(it) },
            onDateSelected = { serviceViewModel.onDateSelected(it) },
            onTimeSelected = { serviceViewModel.onTimeSelected(it) },
            onNotesChange = { serviceViewModel.onNotesChange(it) },
            onConfirm = { serviceViewModel.createAppointment(service!!.id) }
        )
    }
}

@Composable
fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF7A5DE8).copy(alpha = 0.1f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFF7A5DE8),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF7A5DE8)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDialog(
    service: com.example.pethome.data.model.VeterinaryService,
    pets: List<Pet>,
    formState: com.example.pethome.viewmodel.AppointmentFormState,
    onDismiss: () -> Unit,
    onPetSelected: (String) -> Unit,
    onDateSelected: (Long) -> Unit,
    onTimeSelected: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val timeSlots = listOf(
        "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
        "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Reservar: ${service.name}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Selector de mascota
                Text("Selecciona tu mascota", fontWeight = FontWeight.SemiBold)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = pets.find { it.id == formState.selectedPetId }?.name ?: "Seleccionar",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7A5DE8)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        pets.forEach { pet ->
                            DropdownMenuItem(
                                text = { Text(pet.name) },
                                onClick = {
                                    onPetSelected(pet.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Selector de hora (simplificado)
                Text("Selecciona la hora", fontWeight = FontWeight.SemiBold)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    timeSlots.chunked(4).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { time ->
                                FilterChip(
                                    selected = formState.selectedTime == time,
                                    onClick = {
                                        onTimeSelected(time)
                                        onDateSelected(System.currentTimeMillis() + 86400000) // Mañana
                                    },
                                    label = { Text(time, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Notas
                OutlinedTextField(
                    value = formState.notes,
                    onValueChange = onNotesChange,
                    label = { Text("Notas (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF7A5DE8)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !formState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A5DE8))
            ) {
                if (formState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Confirmar Reserva")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
