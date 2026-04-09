package com.example.golazo_store.presentation.admin_pedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.example.golazo_store.domain.model.PedidoAdmin
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AdminPedidosScreen(
    viewModel: AdminPedidosViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(AdminPedidosEvent.LoadPedidos)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            AdminPedidosTopBar(onNavigateBack = onNavigateBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "FILTRAR POR ESTADO",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.filters) { filter ->
                    FilterChip(
                        filter = filter,
                        isSelected = state.selectedFilter == filter,
                        onClick = { viewModel.onEvent(AdminPedidosEvent.SelectFilter(filter)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
                }
            } else {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pedidos Recientes",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            items(state.filteredPedidos) { pedido ->
                                AdminPedidoItem(
                                    pedido = pedido,
                                    onClick = { onNavigateToDetail(pedido.id) }
                                )
                                Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPedidosTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Pedidos",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

@Composable
fun FilterChip(
    filter: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = filter,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AdminPedidoItem(
    pedido: PedidoAdmin,
    onClick: () -> Unit
) {
    val format = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 0
    }
    
    val statusColor = when (pedido.estado) {
        "Pendiente" -> MaterialTheme.colorScheme.tertiaryContainer
        "Enviado" -> MaterialTheme.colorScheme.primaryContainer
        "Completado" -> MaterialTheme.colorScheme.secondaryContainer
        "Cancelado" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val statusTextColor = when (pedido.estado) {
        "Pendiente" -> MaterialTheme.colorScheme.onTertiaryContainer
        "Enviado" -> MaterialTheme.colorScheme.onPrimaryContainer
        "Completado" -> MaterialTheme.colorScheme.onSecondaryContainer
        "Cancelado" -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val totalItems = pedido.items.sumOf { it.cantidad }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ReceiptLong,
                contentDescription = "Receipt",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pedido.numeroPedido,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = pedido.estado.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = statusTextColor
                    )
                }
            }
            
            Text(
                text = pedido.usuario?.nombre ?: "Cliente Desconocido",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${format.format(pedido.total)} \n$totalItems ARTÍCULOS",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = "Ver Detalle",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
