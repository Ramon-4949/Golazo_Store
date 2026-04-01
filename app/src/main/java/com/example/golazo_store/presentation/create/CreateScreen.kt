package com.example.golazo_store.presentation.create

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.golazo_store.ui.theme.primaryDark
import kotlinx.coroutines.launch


@Composable
fun CreateScreen(
    viewModel: CreateViewModel = hiltViewModel(),
    onBack: () -> Unit,
    bottomNavigation: @Composable () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CreateBodyScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack,
        bottomNavigation = bottomNavigation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBodyScreen(
    state: CreateUiState,
    onEvent: (CreateEvent) -> Unit,
    onBack: () -> Unit,
    bottomNavigation: @Composable () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                onEvent(CreateEvent.ClearMessages)
                onBack() // Navegar atrás luego de éxito
            }
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                onEvent(CreateEvent.ClearMessages)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.isEditing) "Editar Camiseta" else "Crear Camiseta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) }, // To perfectly center title
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = bottomNavigation,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            ImagePickerSection(
                imageUri = state.imageUri,
                onImagePicked = { uri -> onEvent(CreateEvent.OnImagePicked(uri)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            FormSectionTitle("Información General")
            
            CustomTextField(
                label = "Nombre de la camiseta",
                value = state.nombre,
                onValueChange = { onEvent(CreateEvent.OnNombreChange(it)) },
                placeholder = "Ej. Camiseta Local 24/25"
            )

            CustomTextField(
                label = "Descripción",
                value = state.descripcion,
                onValueChange = { onEvent(CreateEvent.OnDescripcionChange(it)) },
                placeholder = "Detalles de la tela, ajuste, etc.",
                singleLine = false,
                modifier = Modifier.height(100.dp)
            )

            CategoryDropdown(
                categorias = state.categorias,
                selectedCategoriaId = state.selectedCategoriaId,
                onCategoriaSelected = { onEvent(CreateEvent.OnCategoriaSelected(it)) }
            )

            CustomTextField(
                label = "Precio (€)",
                value = state.precio,
                onValueChange = { onEvent(CreateEvent.OnPrecioChange(it)) },
                placeholder = "$ 0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))
            FormSectionTitle("Gestión de Stock por Talla")

            StockRow(label = "S", value = state.stockS, onValueChange = { onEvent(CreateEvent.OnStockSChange(it)) })
            StockRow(label = "M", value = state.stockM, onValueChange = { onEvent(CreateEvent.OnStockMChange(it)) })
            StockRow(label = "L", value = state.stockL, onValueChange = { onEvent(CreateEvent.OnStockLChange(it)) })
            StockRow(label = "XL", value = state.stockXL, onValueChange = { onEvent(CreateEvent.OnStockXLChange(it)) })
            StockRow(label = "2XL", value = state.stock2XL, onValueChange = { onEvent(CreateEvent.OnStock2XLChange(it)) })

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onEvent(CreateEvent.SaveProduct) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryDark),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (state.isEditing) "Editar publicación" else "Crear publicación", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Cancelar",
                color = Color(0xFF6B7A93),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ImagePickerSection(
    imageUri: android.net.Uri?,
    onImagePicked: (android.net.Uri?) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: android.net.Uri? ->
        onImagePicked(uri)
    }

    val stroke = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Dashed border background
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = Color(0xFFFFD54F).copy(alpha = 0.5f), // Light yellow dash
                style = stroke,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
            )
        }
        
        // Inner content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFFFFDF5)), // Very light yellow background
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Selected Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Small remove/change button overlay
                IconButton(
                    onClick = { onImagePicked(null) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(50))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Remove") // Simple icon reuse
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = "Camera",
                        tint = primaryDark,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sin imagen de producto",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sube una foto de la camiseta para la tienda",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { 
                            launcher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryDark),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Subir Imagen", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FormSectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp,
        color = Color(0xFF2B2B2B),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
}

@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color(0xFF2B2B2B),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF90A4AE)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryDark,
                unfocusedBorderColor = Color(0xFFFFF0EC), // Light border
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categorias: List<com.example.golazo_store.domain.model.Categoria>,
    selectedCategoriaId: Int?,
    onCategoriaSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedText = categorias.find { it.id == selectedCategoriaId }?.nombre ?: "Selecciona una categoría"

    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = "Categoría",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color(0xFF2B2B2B),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryDark,
                    unfocusedBorderColor = Color(0xFFFFF0EC),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                categorias.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(text = categoria.nombre, color = Color.Black) },
                        onClick = {
                            onCategoriaSelected(categoria.id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StockRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Size box
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(48.dp)
                .background(Color(0xFFFFFDF5), RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .clickable { /* No action needed */ },
            contentAlignment = Alignment.Center
        ) {
            // we create a border matching Figma: yellow border
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 1.dp.toPx()
                drawRoundRect(
                    color = Color(0xFFFFD54F),
                    style = Stroke(width = strokeWidth),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
                )
            }
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                color = if (label.contains("X")) Color(0xFF9FA8B8) else Color.Black,
                fontSize = 14.sp
            )
        }
        
        // Input textfield
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("0", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryDark,
                unfocusedBorderColor = Color(0xFFFFF0EC),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}
