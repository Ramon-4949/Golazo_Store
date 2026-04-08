package com.example.golazo_store.presentation.rastrear_pedido

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.golazo_store.domain.model.PedidoAdmin
import com.example.golazo_store.ui.theme.primaryDark
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RastrearPedidoScreen(
    viewModel: RastrearPedidoViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Rastrear Pedido", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally))
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryDark)
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.error!!, color = Color.Red)
            }
        } else if (state.pedido != null) {
            val pedido = state.pedido!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "ESTADO DEL ENVÍO",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pedido ${if(pedido.numeroPedido.isNotEmpty()) "#${pedido.numeroPedido}" else "#ORD-${pedido.id}"}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Historial del envío",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                OrderTimeline(pedido)
                
                Spacer(modifier = Modifier.weight(1f))
                Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
                
                // Address Section
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dirección de entrega", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        val nombreCliente = pedido.usuario?.nombre?.takeIf { it.isNotBlank() } ?: "Cliente General"
                        Text(text = nombreCliente, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        val direccionText = if (pedido.direccion != null) {
                            "${pedido.direccion.calleNumero}, ${pedido.direccion.codigoPostal} ${pedido.direccion.ciudad}, ${pedido.direccion.provincia}"
                        } else if (!pedido.direccionEnvio.isNullOrBlank()) {
                            pedido.direccionEnvio
                        } else {
                            "Dirección no disponible"
                        }
                        Text(text = direccionText, fontSize = 14.sp, color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun OrderTimeline(pedido: PedidoAdmin) {
    val currentStateRaw = pedido.estado.uppercase()
    val isCanceled = currentStateRaw == "CANCELADO"
    
    val currentIndex = when {
        currentStateRaw == "COMPLETADO" -> 2
        currentStateRaw == "ENVIADO" -> 1
        isCanceled -> 2 // If canceled, it replaces the end of timeline
        else -> 0
    }
    
    val formattedDate = try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = parser.parse(pedido.fechaPedido)
        if (date != null) {
            SimpleDateFormat("dd MMM, hh:mm a", Locale("es", "ES")).format(date)
        } else {
            pedido.fechaPedido.take(10)
        }
    } catch(e: Exception) {
         "Reciente"
    }

    Column {
        TimelineItem(
            isCompleted = currentIndex >= 0,
            isLast = false,
            title = "Pendiente",
            subtitle = if(currentIndex == 0 && !isCanceled) "Tu pedido está siendo procesado" else formattedDate,
            isCurrent = currentIndex == 0 && !isCanceled,
            isCanceled = false
        )
        if (isCanceled) {
            TimelineItem(
                isCompleted = true,
                isLast = true,
                title = "Cancelado",
                subtitle = "El pedido ha sido cancelado",
                isCurrent = true,
                isCanceled = true
            )
        } else {
            TimelineItem(
                isCompleted = currentIndex >= 1,
                isLast = false,
                title = "Enviado",
                subtitle = if(currentIndex == 1) "El repartidor está de camino" else "",
                isCurrent = currentIndex == 1,
                isTruck = true,
                isCanceled = false
            )
            TimelineItem(
                isCompleted = currentIndex >= 2,
                isLast = true,
                title = "Completado",
                subtitle = if(currentIndex == 2) "Pedido entregado con éxito" else "Pendiente de entrega",
                isCurrent = currentIndex == 2,
                isCanceled = false
            )
        }
    }
}

@Composable
fun TimelineItem(
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLast: Boolean,
    title: String,
    subtitle: String,
    isTruck: Boolean = false,
    isCanceled: Boolean = false
) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(30.dp)) {
            // Icon or Circle
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCanceled -> Color(0xFFFFEBEE) // Light Red
                            isCurrent && isTruck -> Color(0xFFFFF7E6) // Light orange logic 
                            title == "Completado" && isCompleted -> Color(0xFFE8F5E9) // Light Green
                            isCompleted || isCurrent -> Color(0xFF212529) // Dark fulfilled
                            else -> Color.White
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (isCompleted || isCurrent) Color.Transparent else Color(0xFFDEE2E6),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCanceled) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                } else if (isCurrent && isTruck) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = primaryDark, modifier = Modifier.size(14.dp))
                } else if (title == "Completado" && isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                } else if (isCompleted || isCurrent) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(if (isCompleted && !isCurrent && !isCanceled) Color(0xFF212529) else Color(0xFFDEE2E6))
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = when {
                    isCanceled -> Color.Red
                    title == "Completado" && isCompleted -> Color(0xFF4CAF50)
                    isCurrent && isTruck -> primaryDark
                    isCompleted || isCurrent -> Color.Black
                    else -> Color.Gray
                }
            )
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
