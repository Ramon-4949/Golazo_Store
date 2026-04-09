package com.example.golazo_store.presentation.mis_pedidos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.golazo_store.domain.model.PedidoAdmin
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPedidosScreen(
    viewModel: MisPedidosViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToTrack: (Int) -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tabs = listOf("Todos", "En curso", "Finalizados")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Mis Pedidos", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally))
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = state.currentTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    if (state.currentTab < tabPositions.size) {
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[state.currentTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = state.currentTab == index,
                        onClick = { viewModel.setTab(index) },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (state.currentTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (state.currentTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
                }
            } else {
                val filteredPedidos = state.pedidos.filter { pedido ->
                    val isFinalizado = pedido.estado.equals("Entregado", ignoreCase = true) || 
                                       pedido.estado.equals("Completado", ignoreCase = true) ||
                                       pedido.estado.equals("Cancelado", ignoreCase = true)
                    when (state.currentTab) {
                        1 -> !isFinalizado // En curso
                        2 -> isFinalizado  // Finalizados
                        else -> true       // Todos
                    }
                }
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredPedidos) { pedido ->
                        PedidoCard(
                            pedido = pedido,
                            onTrackClicked = { onNavigateToTrack(pedido.id) },
                            onDetailClicked = { onNavigateToDetail(pedido.id) } // In the future they might want this
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PedidoCard(
    pedido: PedidoAdmin,
    onTrackClicked: () -> Unit,
    onDetailClicked: () -> Unit
) {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "DO")).apply {
        maximumFractionDigits = 0
    }
    
    // Attempt to format ISO date safely
    val formattedDate = try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = parser.parse(pedido.fechaPedido)
        if (date != null) {
            SimpleDateFormat("dd MMM yyyy", Locale("es", "ES")).format(date)
        } else {
            pedido.fechaPedido.take(10)
        }
    } catch (e: Exception) {
        pedido.fechaPedido.take(10)
    }
    
    val artText = if (pedido.items.size == 1) "1 artículo" else "${pedido.items.size} artículos"
    val isEnCamino = pedido.estado.equals("EN CAMINO", ignoreCase = true)
    val isEntregado = pedido.estado.equals("ENTREGADO", ignoreCase = true) || pedido.estado.equals("COMPLETADO", ignoreCase = true)

    val badgeColor = when {
        isEnCamino -> MaterialTheme.colorScheme.primaryContainer
        isEntregado -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val badgeTextColor = when {
        isEnCamino -> MaterialTheme.colorScheme.onPrimaryContainer
        isEntregado -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onDetailClicked() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Estado
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = pedido.estado.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor
                    )
                }
                
                // Precio Total
                Text(
                    text = format.format(pedido.total),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Pedido ${if(pedido.numeroPedido.isNotEmpty()) "#${pedido.numeroPedido}" else "#ORD-${pedido.id}"}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$formattedDate • $artText",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Thumbnail images
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(pedido.items.take(4)) { item ->
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                         val imageUrl = item.camiseta?.imagenUrl
                         if (!imageUrl.isNullOrBlank()) {
                             val fullUrl = if (imageUrl.startsWith("http")) imageUrl else "http://golazostoreapi.somee.com\$imageUrl"
                             AsyncImage(
                                 model = ImageRequest.Builder(LocalContext.current)
                                     .data(fullUrl)
                                     .crossfade(true)
                                     .build(),
                                 contentDescription = null,
                                 contentScale = ContentScale.Fit,
                                 modifier = Modifier.fillMaxSize().padding(4.dp)
                             )
                         } else {
                             // Fallback if no image
                             Text("Img", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                         }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Buttons row
            if (isEnCamino) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onTrackClicked,
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Rastrear", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { /* More actions */ },
                        modifier = Modifier.size(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = "Más", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            } else {
                Button(
                    onClick = if (isEntregado) { { /* Buy again action */ } } else onDetailClicked,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text(
                        text = if (isEntregado) "Comprar de nuevo" else "Ver detalles",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
