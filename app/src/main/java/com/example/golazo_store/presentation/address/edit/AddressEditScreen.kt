package com.example.golazo_store.presentation.address.edit

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.golazo_store.ui.theme.primaryDark

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
                        color = Color(0xFF07152B)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ATRÁS",
                            tint = Color(0xFF07152B)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF9F9FB)
                )
            )
        },
        containerColor = Color(0xFFF9F9FB)
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
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Nombre de la Dirección
                AddressTextField(
                    label = "Nombre de la dirección (ej. Casa, Trabajo)",
                    value = state.nombreDireccion,
                    placeholder = "Ej. Mi Casa",
                    onValueChange = { onEvent(AddressEditEvent.OnNombreDireccionChange(it)) }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Calle
                AddressTextField(
                    label = "Calle",
                    value = state.calleNumero,
                    placeholder = "Av. Paseo de la Reforma 123",
                    onValueChange = { onEvent(AddressEditEvent.OnCalleNumeroChange(it)) }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Provincia
                AddressTextField(
                    label = "Provincia",
                    value = state.provincia,
                    placeholder = "Duarte",
                    onValueChange = { onEvent(AddressEditEvent.OnProvinciaChange(it)) }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // CP y Ciudad en Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AddressTextField(
                        label = "Código Postal",
                        value = state.codigoPostal,
                        placeholder = "31000",
                        onValueChange = { onEvent(AddressEditEvent.OnCodigoPostalChange(it)) },
                        modifier = Modifier.weight(1f)
                    )
                    AddressTextField(
                        label = "Ciudad",
                        value = state.ciudad,
                        placeholder = "Santiago",
                        onValueChange = { onEvent(AddressEditEvent.OnCiudadChange(it)) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

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
                        colors = CheckboxDefaults.colors(checkedColor = primaryDark)
                    )
                    Text(
                        text = "Guardar esta direccion como principal",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7A90)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onEvent(AddressEditEvent.OnSave) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryDark),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Guardar Dirección",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Guardar",
                            tint = Color.Black
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
            color = Color(0xFF07152B)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().height(if (singleLine) 56.dp else (lines * 40).dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedBorderColor = primaryDark,
                cursorColor = primaryDark
            ),
            singleLine = singleLine,
            maxLines = if (singleLine) 1 else lines
        )
    }
}
