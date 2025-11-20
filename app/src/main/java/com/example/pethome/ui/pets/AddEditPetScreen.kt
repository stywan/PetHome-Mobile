package com.example.pethome.ui.pets

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.pethome.viewmodel.PetViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPetScreen(
    viewModel: PetViewModel,
    onNavigateBack: () -> Unit,
    isEditing: Boolean = false
) {
    val formState by viewModel.formState.collectAsState()

    LaunchedEffect(formState.isSuccess) {
        if (formState.isSuccess) {
            viewModel.clearForm()
            viewModel.resetSuccessState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Editar Mascota" else "Agregar Mascota",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearForm()
                        onNavigateBack()
                    }) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Card con selector de foto
            PetPhotoSelector(viewModel = viewModel, isEditing = isEditing)

            // Información Básica
            SectionTitle("Información Básica")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StyledTextField(
                        value = formState.name,
                        onValueChange = { viewModel.onNameChange(it) },
                        label = "Nombre",
                        icon = Icons.Default.Pets,
                        error = formState.nameError,
                        placeholder = "Ej: Firulais"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            StyledTextField(
                                value = formState.species,
                                onValueChange = { viewModel.onSpeciesChange(it) },
                                label = "Especie",
                                icon = Icons.Default.Category,
                                error = formState.speciesError,
                                placeholder = "Ej: Perro"
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            StyledTextField(
                                value = formState.breed,
                                onValueChange = { viewModel.onBreedChange(it) },
                                label = "Raza",
                                icon = Icons.Default.Info,
                                error = formState.breedError,
                                placeholder = "Ej: Labrador"
                            )
                        }
                    }

                    StyledTextField(
                        value = formState.color,
                        onValueChange = { viewModel.onColorChange(it) },
                        label = "Color",
                        icon = Icons.Default.Palette,
                        error = formState.colorError,
                        placeholder = "Ej: Marrón"
                    )
                }
            }

            // Características Físicas
            SectionTitle("Características Físicas")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            StyledTextField(
                                value = formState.age,
                                onValueChange = { viewModel.onAgeChange(it) },
                                label = "Edad (años)",
                                icon = Icons.Default.Cake,
                                error = formState.ageError,
                                keyboardType = KeyboardType.Number,
                                placeholder = "Ej: 3"
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            StyledTextField(
                                value = formState.weight,
                                onValueChange = { viewModel.onWeightChange(it) },
                                label = "Peso (kg)",
                                icon = Icons.Default.Scale,
                                error = formState.weightError,
                                keyboardType = KeyboardType.Decimal,
                                placeholder = "Ej: 25.5"
                            )
                        }
                    }

                    GenderSelector(
                        selectedGender = formState.gender,
                        onGenderSelected = { viewModel.onGenderChange(it) },
                        error = formState.genderError
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mensaje de error
            if (formState.errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
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
                        IconButton(onClick = { viewModel.clearError() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color(0xFFD32F2F)
                            )
                        }
                    }
                }
            }

            // Botón guardar
            Button(
                onClick = { viewModel.savePet() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7A5DE8)
                ),
                enabled = !formState.isLoading,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                if (formState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        if (isEditing) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEditing) "Actualizar Mascota" else "Guardar Mascota",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PetPhotoSelector(viewModel: PetViewModel, isEditing: Boolean) {
    val context = LocalContext.current
    val formState by viewModel.formState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onImageSelected(it.toString()) }
    }

    // Launcher para cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri?.let { viewModel.onImageSelected(it.toString()) }
        }
    }

    // Crear archivo temporal para la cámara
    fun createTempImageUri(): Uri {
        val tempFile = File.createTempFile("pet_photo_", ".jpg", context.cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto de la mascota
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7A5DE8).copy(alpha = 0.1f))
                    .border(3.dp, Color(0xFF7A5DE8).copy(alpha = 0.3f), CircleShape)
                    .clickable { showDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (formState.imageUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(formState.imageUrl),
                        contentDescription = "Foto de mascota",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Pets,
                        contentDescription = null,
                        tint = Color(0xFF7A5DE8),
                        modifier = Modifier.size(50.dp)
                    )
                }

                // Botón de cámara flotante
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = (-4).dp, y = (-4).dp),
                    shape = CircleShape,
                    color = Color(0xFF7A5DE8),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Cambiar foto",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (formState.imageUrl != null) "Toca para cambiar la foto" else "Toca para agregar una foto",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF7A5DE8),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isEditing) "Actualiza los datos de tu mascota" else "Completa la información de tu nueva mascota",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }

    // Diálogo para elegir fuente de imagen
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    "Seleccionar foto",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Opción cámara
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                tempImageUri = createTempImageUri()
                                cameraLauncher.launch(tempImageUri!!)
                                showDialog = false
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color(0xFF7A5DE8),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Tomar foto", fontWeight = FontWeight.Medium)
                        }
                    }

                    // Opción galería
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                galleryLauncher.launch("image/*")
                                showDialog = false
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = Color(0xFF7A5DE8),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Elegir de galería", fontWeight = FontWeight.Medium)
                        }
                    }

                    // Opción eliminar (solo si hay foto)
                    if (formState.imageUrl != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onImageSelected(null)
                                    showDialog = false
                                },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text("Eliminar foto", fontWeight = FontWeight.Medium, color = Color(0xFFD32F2F))
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2D2D2D),
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    placeholder: String = ""
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth(),
            isError = error != null,
            singleLine = true,
            leadingIcon = {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (error != null) MaterialTheme.colorScheme.error else Color(0xFF7A5DE8)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7A5DE8),
                focusedLabelColor = Color(0xFF7A5DE8),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                errorBorderColor = MaterialTheme.colorScheme.error
            )
        )
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderSelector(
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    error: String? = null
) {
    Column {
        Text(
            text = "Género",
            fontSize = 12.sp,
            color = if (error != null) MaterialTheme.colorScheme.error else Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Opción Male
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onGenderSelected("Male") },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedGender == "Male")
                        Color(0xFF7A5DE8)
                    else
                        Color(0xFFF5F5F5)
                ),
                border = if (error != null)
                    BorderStroke(2.dp, MaterialTheme.colorScheme.error)
                else
                    null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Male,
                        contentDescription = "Macho",
                        tint = if (selectedGender == "Male") Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Macho",
                        color = if (selectedGender == "Male") Color.White else Color.Gray,
                        fontWeight = if (selectedGender == "Male") FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            // Opción Female
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onGenderSelected("Female") },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedGender == "Female")
                        Color(0xFF7A5DE8)
                    else
                        Color(0xFFF5F5F5)
                ),
                border = if (error != null)
                    BorderStroke(2.dp, MaterialTheme.colorScheme.error)
                else
                    null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Female,
                        contentDescription = "Hembra",
                        tint = if (selectedGender == "Female") Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hembra",
                        color = if (selectedGender == "Female") Color.White else Color.Gray,
                        fontWeight = if (selectedGender == "Female") FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
