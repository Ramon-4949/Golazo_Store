package com.example.golazo_store.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.golazo_store.domain.model.Camiseta
import java.text.NumberFormat
import java.util.Locale
import com.example.golazo_store.ui.theme.primaryDark
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

@Composable
fun CamisetaDetailScreen(
    viewModel: CamisetaDetailViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CamisetaDetailBodyScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CamisetaDetailBodyScreen(
    state: CamisetaDetailUiState,
    onEvent: (CamisetaDetailEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Golazo Store",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color(0xFF07152B),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
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
                actions = {
                    // Spacer for centering title properly (matching navigationIcon width)
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF9F9FB)
                )
            )
        },
        containerColor = Color(0xFFF9F9FB)
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryDark)
            }
        } else if (state.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.errorMessage, color = Color.Red, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { onEvent(CamisetaDetailEvent.RetryLoading) }) {
                        Text("Reintentar")
                    }
                }
            }
        } else if (state.camiseta != null) {
            CamisetaDetailContent(
                camiseta = state.camiseta,
                selectedSize = state.selectedSize,
                paddingValues = paddingValues,
                onEvent = onEvent
            )
        }
    }
}

@Composable
fun CamisetaDetailContent(
    camiseta: Camiseta,
    selectedSize: String?,
    paddingValues: PaddingValues,
    onEvent: (CamisetaDetailEvent) -> Unit
) {
    val scrollState = rememberScrollState()
    val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
    format.maximumFractionDigits = 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
            .background(Color(0xFFF9F9FB))
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        // Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF0F0F0)),
            contentAlignment = Alignment.Center
        ) {
            val imageUrl = camiseta.imagenUrl.takeIf { it?.isNotBlank() == true }
            if (imageUrl != null) {
                val fullUrl = if (imageUrl.startsWith("http")) imageUrl else "http://golazostoreapi.somee.com\$imageUrl"
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(fullUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = camiseta.nombre,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title and Price
        Text(
            text = camiseta.nombre,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Color(0xFF07152B)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = format.format(camiseta.precio),
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            color = Color(0xFF07152B)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = camiseta.descripcion,
            fontSize = 14.sp,
            color = Color.DarkGray,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Size Selection
        Text(
            text = "SELECCIONA TU TALLA",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color(0xFF07152B)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        val sizes = listOf("S" to camiseta.stockS, "M" to camiseta.stockM, "L" to camiseta.stockL, "XL" to camiseta.stockXL, "XXL" to camiseta.stock2XL)
        val availableSizes = sizes.filter { it.second > 0 }.map { it.first }
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(if (availableSizes.isNotEmpty()) availableSizes else listOf("S", "M", "L", "XL")) { size ->
                SizeChip(
                    size = size,
                    isSelected = selectedSize == size,
                    onClick = { onEvent(CamisetaDetailEvent.SelectSize(size)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Info bullets
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.LocalShipping, 
                contentDescription = "Shipping",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Envío gratis en pedidos superiores a \$150",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.CheckCircleOutline, 
                contentDescription = "Authentic",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Producto 100% auténtico con licencia oficial",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Add to Cart Buttons
        Button(
            onClick = { onEvent(CamisetaDetailEvent.AddToCart(camiseta.id)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryDark),
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = "Add to Cart",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Añadir al Carrito",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SizeChip(
    size: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 64.dp, height = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFFFF7E6) else Color.White)
            .border(
                width = 1.dp,
                color = if (isSelected) primaryDark else Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = size,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) primaryDark else Color.DarkGray,
            fontSize = 14.sp
        )
    }
}
