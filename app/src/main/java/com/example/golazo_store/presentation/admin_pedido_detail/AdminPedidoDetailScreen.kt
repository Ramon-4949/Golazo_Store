package com.example.golazo_store.presentation.admin_pedido_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.golazo_store.domain.model.ItemPedidoAdmin
import com.example.golazo_store.domain.model.PedidoAdmin
import com.example.golazo_store.ui.theme.primaryDark
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AdminPedidoDetailScreen(
    viewModel: AdminPedidoDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AdminPedidoDetailTopBar(
                pedidoNumber = state.pedido?.numeroPedido ?: "...",
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            if (state.pedido != null) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { viewModel.onEvent(AdminPedidoDetailEvent.SaveChanges) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryDark),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !state.isUpdating
                    ) {
                        if (state.isUpdating) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(imageVector = Icons.Default.Save, contentDescription = "Guardar", tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Guardar Cambios", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.error!!, color = Color.Red)
            }
        } else if (state.pedido != null) {
            PedidoDetailContent(
                pedido = state.pedido!!,
                state = state,
                viewModel = viewModel,
                paddingValues = paddingValues
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPedidoDetailTopBar(pedidoNumber: String, onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Detalle del Pedido",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = pedidoNumber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.Black
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun PedidoDetailContent(
    pedido: PedidoAdmin,
    state: AdminPedidoDetailUiState,
    viewModel: AdminPedidoDetailViewModel,
    paddingValues: PaddingValues
) {
    val format = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 0
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(paddingValues)
    ) {
        item {
            SectionTitle(title = "INFORMACIÓN DEL CLIENTE")
            ClientInfoSection(pedido)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SectionTitle(title = "ESTADO DEL PEDIDO")
            StatusDropdownSection(state, viewModel)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SectionTitle(title = "PRODUCTOS (${pedido.items.size})")
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(pedido.items) { item ->
            ProductItemRow(item, format)
            Divider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle(title = "RESUMEN DEL PAGO")
            PaymentSummarySection(pedido, format)
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.Gray,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun ClientInfoSection(pedido: PedidoAdmin) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFFFF7E6), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Person, contentDescription = "Cliente", tint = Color(0xFFD4A373))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = pedido.usuario?.nombre?.takeIf { it.isNotBlank() } ?: "Sin nombre",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Text(
                text = pedido.usuario?.correo?.takeIf { it.isNotBlank() } ?: "Sin correo",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0xFFF8F9FA), RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Dirección", tint = Color(0xFFEAA121))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "DIRECCIÓN DE ENVÍO",
                fontSize = 10.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            val direccionText = if (pedido.direccion != null) {
                "${pedido.direccion.calleNumero}, ${pedido.direccion.codigoPostal} ${pedido.direccion.ciudad}, ${pedido.direccion.provincia}"
            } else if (!pedido.direccionEnvio.isNullOrBlank()) {
                pedido.direccionEnvio
            } else {
                "Dirección no disponible"
            }
            Text(
                text = direccionText,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusDropdownSection(state: AdminPedidoDetailUiState, viewModel: AdminPedidoDetailViewModel) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = state.selectedEstado,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryDark,
                    unfocusedBorderColor = primaryDark.copy(alpha = 0.5f)
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                state.availableEstados.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            viewModel.onEvent(AdminPedidoDetailEvent.SelectEstado(selectionOption))
                            expanded = false
                        }
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
    
    val statusColor = when (state.pedido?.estado) {
        "Pendiente" -> Color(0xFFFFF3CD)
        "Enviado" -> Color(0xFFD0E2FF)
        "Completado" -> Color(0xFFD4EDDA)
        "Cancelado" -> Color(0xFFF8D7DA)
        else -> Color.LightGray
    }
    
    val statusTextColor = when (state.pedido?.estado) {
        "Pendiente" -> Color(0xFF856404)
        "Enviado" -> Color(0xFF0043CE)
        "Completado" -> Color(0xFF155724)
        "Cancelado" -> Color(0xFF721C24)
        else -> Color.DarkGray
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(statusColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "ESTADO ACTUAL: ${state.pedido?.estado?.uppercase()}",
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = statusTextColor
        )
    }
}

@Composable
fun ProductItemRow(item: ItemPedidoAdmin, format: NumberFormat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color(0xFF1E2329), RoundedCornerShape(8.dp))
        ) {
            val imageUrl = item.camiseta?.imagenUrl
            if (!imageUrl.isNullOrBlank()) {
                val fullUrl = if (imageUrl.startsWith("http")) imageUrl else "http://golazostoreapi.somee.com\$imageUrl"
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(fullUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.camiseta?.nombre,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.camiseta?.nombre ?: "Producto desconocido",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Cantidad: ${item.cantidad}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = format.format(item.precioUnitario),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun PaymentSummarySection(pedido: PedidoAdmin, format: NumberFormat) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0xFFF8F9FA), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Subtotal", color = Color.Gray, fontSize = 14.sp)
            Text(format.format(pedido.total), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Envío (Standard)", color = Color.Gray, fontSize = 14.sp)
            Text("Gratis", color = Color(0xFF28A745), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(format.format(pedido.total), color = Color.Black, fontWeight = FontWeight.Black, fontSize = 20.sp)
        }
    }
}
