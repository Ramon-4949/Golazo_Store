package com.example.golazo_store.presentation.address.edit

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


val provinciasDR = listOf(
    "Azua", "Baoruco", "Barahona", "Dajabón", "Distrito Nacional", "Duarte",
    "Elías Piña", "El Seibo", "Espaillat", "Hato Mayor", "Hermanas Mirabal",
    "Independencia", "La Altagracia", "La Romana", "La Vega", "María Trinidad Sánchez",
    "Monseñor Nouel", "Monte Cristi", "Monte Plata", "Pedernales", "Peravia",
    "Puerto Plata", "Samaná", "Sánchez Ramírez", "San Cristóbal", "San José de Ocoa",
    "San Juan", "San Pedro de Macorís", "Santiago", "Santiago Rodríguez",
    "Santo Domingo", "Valverde"
)

@Composable
fun AddressEditScreen(
    viewModel: AddressEditViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            Toast.makeText(context, "Dirección guardada correctamente", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    AddressEditBodyScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressEditBodyScreen(
    state: AddressEditUiState,
    onEvent: (AddressEditEvent) -> Unit,
    onBack: () -> Unit
) {
    val title = if (state.id == null || state.id == -1) "Nueva Dirección" else "Editar Dirección"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ATRÁS",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Nombre de la Dirección
                AddressTextField(
                    label = "Nombre de la dirección (ej. Casa, Trabajo)",
                    value = state.nombreDireccion,
                    placeholder = "Ej. Mi Casa",
                    error = state.nombreError,
                    onValueChange = { onEvent(AddressEditEvent.OnNombreDireccionChange(it)) }
                )

                // Calle
                AddressTextField(
                    label = "Calle",
                    value = state.calleNumero,
                    placeholder = "Av. Paseo de la Reforma 123",
                    error = state.calleError,
                    onValueChange = { onEvent(AddressEditEvent.OnCalleNumeroChange(it)) }
                )

                // Provincia
                AddressProvinceDropdown(
                    label = "Provincia",
                    value = state.provincia,
                    placeholder = "Seleccione una provincia",
                    error = state.provinciaError,
                    provincias = provinciasDR,
                    onValueChange = { onEvent(AddressEditEvent.OnProvinciaChange(it)) }
                )

                // CP y Ciudad en Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AddressTextField(
                        label = "Código Postal",
                        value = state.codigoPostal,
                        placeholder = "31000",
                        error = state.codigoPostalError,
                        onValueChange = { onEvent(AddressEditEvent.OnCodigoPostalChange(it)) },
                        modifier = Modifier.weight(1f)
                    )
                    AddressTextField(
                        label = "Ciudad",
                        value = state.ciudad,
                        placeholder = "Santiago",
                        error = state.ciudadError,
                        onValueChange = { onEvent(AddressEditEvent.OnCiudadChange(it)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Referencia
                AddressTextField(
                    label = "Referencia (Opcional)",
                    value = state.reference,
                    placeholder = "Entre qué calles se encuentra, color del portón, etc.",
                    onValueChange = { onEvent(AddressEditEvent.OnReferenceChange(it)) },
                    singleLine = false,
                    lines = 3
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Principal Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = state.esPrincipal,
                        onCheckedChange = { onEvent(AddressEditEvent.OnEsPrincipalChange(it)) },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = "Guardar esta direccion como principal",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onEvent(AddressEditEvent.OnSave) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Guardar Dirección",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Guardar",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun AddressTextField(
    label: String,
    value: String,
    placeholder: String,
    error: String? = null,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    lines: Int = 1
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            isError = error != null,
            supportingText = {
                if (error != null) {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorSupportingTextColor = MaterialTheme.colorScheme.error
            ),
            singleLine = singleLine,
            minLines = if (singleLine) 1 else lines,
            maxLines = if (singleLine) 1 else lines
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressProvinceDropdown(
    label: String,
    value: String,
    placeholder: String,
    error: String?,
    provincias: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                isError = error != null,
                supportingText = {
                    if (error != null) {
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                provincias.forEach { provincia ->
                    DropdownMenuItem(
                        text = { Text(provincia, color = MaterialTheme.colorScheme.onSurface) },
                        onClick = {
                            onValueChange(provincia)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
