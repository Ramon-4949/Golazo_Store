package com.example.golazo_store.presentation.CreateEdit.edit

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
import kotlinx.coroutines.launch


@Composable
fun CreateEditScreen(
    viewModel: CreateEditViewModel = hiltViewModel(),
    onBack: () -> Unit,
    bottomNavigation: @Composable () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CreateEditBodyScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack,
        bottomNavigation = bottomNavigation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditBodyScreen(
    state: CreateEditUiState,
    onEvent: (CreateEditEvent) -> Unit,
    onBack: () -> Unit,
    bottomNavigation: @Composable () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                onEvent(CreateEditEvent.ClearMessages)
                onBack() // Navegar atrás luego de éxito
            }
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                onEvent(CreateEditEvent.ClearMessages)
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = bottomNavigation,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ImagePickerSection(
                imageUri = state.imageUri,
                originalImageUrl = state.originalImageUrl,
                error = state.imageError,
                onImagePicked = { uri -> onEvent(CreateEditEvent.OnImagePicked(uri)) }
            )

            FormSectionTitle("Información General")
            
            CustomTextField(
                label = "Nombre de la camiseta",
                value = state.nombre,
                onValueChange = { onEvent(CreateEditEvent.OnNombreChange(it)) },
                placeholder = "Ej. Camiseta Local 24/25",
                error = state.nombreError
            )

            CustomTextField(
                label = "Descripción",
                value = state.descripcion,
                onValueChange = { onEvent(CreateEditEvent.OnDescripcionChange(it)) },
                placeholder = "Detalles de la tela, ajuste, etc.",
                error = state.descripcionError,
                singleLine = false,
                modifier = Modifier.height(100.dp)
            )

            CategoryDropdown(
                categorias = state.categorias,
                selectedCategoriaId = state.selectedCategoriaId,
                error = state.categoriaError,
                onCategoriaSelected = { onEvent(CreateEditEvent.OnCategoriaSelected(it)) }
            )

            CustomTextField(
                label = "Precio (€)",
                value = state.precio,
                onValueChange = { onEvent(CreateEditEvent.OnPrecioChange(it)) },
                placeholder = "$ 0.00",
                error = state.precioError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            FormSectionTitle("Gestión de Stock por Talla")

            StockRow(label = "S", value = state.stockS, error = state.stockSError, onValueChange = { onEvent(CreateEditEvent.OnStockSChange(it)) })
            StockRow(label = "M", value = state.stockM, error = state.stockMError, onValueChange = { onEvent(CreateEditEvent.OnStockMChange(it)) })
            StockRow(label = "L", value = state.stockL, error = state.stockLError, onValueChange = { onEvent(CreateEditEvent.OnStockLChange(it)) })
            StockRow(label = "XL", value = state.stockXL, error = state.stockXLError, onValueChange = { onEvent(CreateEditEvent.OnStockXLChange(it)) })
            StockRow(label = "2XL", value = state.stock2XL, error = state.stock2XLError, onValueChange = { onEvent(CreateEditEvent.OnStock2XLChange(it)) })

            Button(
                onClick = { onEvent(CreateEditEvent.SaveProduct) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (state.isEditing) "Editar publicación" else "Crear publicación", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Text(
                text = "Cancelar",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun ImagePickerSection(
    imageUri: android.net.Uri?,
    originalImageUrl: String? = null,
    error: String? = null,
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
        val dashColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = dashColor, // Light dash
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
                .background(MaterialTheme.colorScheme.surfaceVariant), // Light background
            contentAlignment = Alignment.Center
        ) {
            val modelImage = imageUri ?: originalImageUrl
            if (modelImage != null && modelImage.toString().isNotBlank()) {
                AsyncImage(
                    model = modelImage,
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
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), RoundedCornerShape(50))
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
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sin imagen de producto",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sube una foto de la camiseta para la tienda",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Subir Imagen", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
    
    if (error != null) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 4.dp),
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun FormSectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurface,
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
    error: String? = null,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            isError = error != null,
            supportingText = {
                if (error != null) {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant, // Light border
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorSupportingTextColor = MaterialTheme.colorScheme.error
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categorias: List<com.example.golazo_store.domain.model.Categoria>,
    selectedCategoriaId: Int?,
    error: String? = null,
    onCategoriaSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedText = categorias.find { it.id == selectedCategoriaId }?.nombre ?: "Selecciona una categoría"

    Column(modifier = Modifier) {
        Text(
            text = "Categoría",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
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
                singleLine = true,
                isError = error != null,
                supportingText = {
                    if (error != null) {
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorSupportingTextColor = MaterialTheme.colorScheme.error
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                categorias.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(text = categoria.nombre, color = MaterialTheme.colorScheme.onSurface) },
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
    error: String? = null,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Size box
        val strokeColor = MaterialTheme.colorScheme.primary
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(48.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .clickable { /* No action needed */ },
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 1.dp.toPx()
                drawRoundRect(
                    color = strokeColor,
                    style = Stroke(width = strokeWidth),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
                )
            }
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                color = if (label.contains("X")) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            )
        }
        
        // Input textfield
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("0", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = error != null,
            supportingText = {
                if (error != null) {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            },
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorSupportingTextColor = MaterialTheme.colorScheme.error
            )
        )
    }
}




