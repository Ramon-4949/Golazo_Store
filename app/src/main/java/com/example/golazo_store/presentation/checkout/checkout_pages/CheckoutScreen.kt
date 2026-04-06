package com.example.golazo_store.presentation.checkout.checkout_pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.golazo_store.domain.model.CartItem
import com.example.golazo_store.domain.model.Direccion
import com.example.golazo_store.domain.model.MetodoPago
import com.example.golazo_store.ui.theme.primaryDark
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSuccess: (String) -> Unit,
    onChangeAddress: (Int?) -> Unit,
    onChangePayment: (Int?) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.createdOrderNumber) {
        if (state.createdOrderNumber != null) {
            onSuccess(state.createdOrderNumber!!)
        }
    }

    if (state.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            title = { Text("Aviso", fontWeight = FontWeight.Bold) },
            text = { Text(state.error!!) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissError() }) {
                    Text("OK", color = primaryDark, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White
        )
    }

    CheckoutBodyScreen(
        state = state,
        onBack = onBack,
        onConfirmOrder = { viewModel.onConfirmOrder() },
        onChangeAddress = onChangeAddress,
        onChangePayment = onChangePayment
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutBodyScreen(
    state: CheckoutUiState,
    onBack: () -> Unit,
    onConfirmOrder: () -> Unit,
    onChangeAddress: (Int?) -> Unit,
    onChangePayment: (Int?) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Finalizar Compra",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = onConfirmOrder,
                        enabled = !state.isPlacingOrder && state.cartItems.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryDark,
                            disabledContainerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        if (state.isPlacingOrder) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                "Confirmar Pedido ->",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->

        if (state.isLoadingData) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryDark)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
            ) {
                // Dirección de Envío
                item {
                    SectionHeader(
                        title = "DIRECCIÓN DE ENVÍO", 
                        actionText = "Cambiar", 
                        onAction = { onChangeAddress(state.direccionPrincipal?.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (state.direccionPrincipal != null) {
                        AddressCard(state.direccionPrincipal)
                    } else {
                        EmptyCardPlaceholder("No hay dirección seleccionada")
                    }
                }

                // Método de Pago
                item {
                    SectionHeader(
                        title = "MÉTODO DE PAGO", 
                        actionText = "Cambiar", 
                        onAction = { onChangePayment(state.metodoPagoPrincipal?.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (state.metodoPagoPrincipal != null) {
                        PaymentCard(state.metodoPagoPrincipal)
                    } else {
                        EmptyCardPlaceholder("No hay método de pago seleccionado")
                    }
                }

                // Tu Pedido
                item {
                    Text(
                        text = "TU PEDIDO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7A90),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(state.cartItems) { item ->
                    CheckoutCartItem(item)
                }

                // Resumen del Pedido
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "RESUMEN DEL PEDIDO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7A90),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val format = NumberFormat.getCurrencyInstance(Locale("en", "US")).apply {
                        maximumFractionDigits = 0
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Subtotal", color = Color(0xFF6B7A90), fontSize = 14.sp)
                        Text(text = format.format(state.subtotal), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Envío", color = Color(0xFF6B7A90), fontSize = 14.sp)
                        Text(text = "Gratis", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF07152B))
                        Text(text = format.format(state.total), fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color(0xFF07152B))
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, actionText: String, onAction: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6B7A90),
            letterSpacing = 1.sp
        )
        Text(
            text = actionText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = primaryDark,
            modifier = Modifier.clickable { onAction() }
        )
    }
}

@Composable
fun AddressCard(direccion: Direccion) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFFFF7DE), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = primaryDark)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = direccion.nombreDireccion,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF07152B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${direccion.calleNumero}\n${direccion.codigoPostal} ${direccion.ciudad}, ${direccion.provincia}",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7A90)
                )
            }
        }
    }
}

@Composable
fun PaymentCard(metodoPago: MetodoPago) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF1E3A8A), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (metodoPago.tipoTarjeta.lowercase().contains("visa")) "VISA" else "MC",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${metodoPago.tipoTarjeta} terminada en ${metodoPago.numeroOculto.takeLast(4)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF07152B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Expira ${metodoPago.mesExpiracion}/${metodoPago.anioExpiracion}",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7A90)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF9EA7B5))
        }
    }
}

@Composable
fun CheckoutCartItem(item: CartItem) {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "US")).apply {
        maximumFractionDigits = 0
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color(0xFFF5F6F8), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = item.imagenUrl,
                contentDescription = item.nombre,
                modifier = Modifier.size(50.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF07152B),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Talla: ${item.talla} • Cantidad: ${item.cantidad}",
                fontSize = 12.sp,
                color = Color(0xFF6B7A90)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = format.format(item.precio),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = Color(0xFF07152B)
            )
        }
    }
}

@Composable
fun EmptyCardPlaceholder(message: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
            Text(message, color = Color.Gray)
        }
    }
}
