package com.example.golazo_store.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun CamisetaDetailScreen(
    viewModel: CamisetaDetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onAddToCartSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(CamisetaDetailEvent.RefreshCamiseta)
    }

    LaunchedEffect(state.isAddedToCart) {
        if (state.isAddedToCart) {
            onAddToCartSuccess()
            viewModel.onEvent(CamisetaDetailEvent.ResetAddToCart)
        }
    }

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
                sizeError = state.sizeError,
                quantity = state.quantity,
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
    sizeError: String?,
    quantity: Int,
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
                val fullUrl = if (imageUrl.startsWith("http")) imageUrl else "http://golazostoreapi.somee.com$imageUrl"
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

        Text(
            text = camiseta.descripcion,
            fontSize = 14.sp,
            color = Color.DarkGray,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))


        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Talla: ${selectedSize ?: ""}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF07152B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            val sizes = listOf(
                "S" to camiseta.stockS,
                "M" to camiseta.stockM,
                "L" to camiseta.stockL,
                "XL" to camiseta.stockXL,
                "2XL" to camiseta.stock2XL
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sizes) { sizeItem ->
                    val hasStock = sizeItem.second > 0
                    SizeChip(
                        size = sizeItem.first,
                        isSelected = selectedSize == sizeItem.first,
                        isError = sizeError != null,
                        isEnabled = hasStock,
                        onClick = { 
                            if (hasStock) onEvent(CamisetaDetailEvent.SelectSize(sizeItem.first)) 
                        }
                    )
                }
            }

            if (sizeError != null) {
                Text(
                    text = sizeError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Disponible",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF2E7D32) // Greenish
            )

            Spacer(modifier = Modifier.height(12.dp))

            val stockDisponible = when(selectedSize) {
                "S" -> camiseta.stockS
                "M" -> camiseta.stockM
                "L" -> camiseta.stockL
                "XL" -> camiseta.stockXL
                "XXL", "2XL" -> camiseta.stock2XL
                else -> 0
            }

            QuantityDropdown(
                quantity = quantity,
                maxStock = stockDisponible,
                isEnabled = selectedSize != null && stockDisponible > 0,
                onQuantitySelected = { onEvent(CamisetaDetailEvent.SelectQuantity(it)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedSize != null) {
                Text(
                    text = "Solo quedan $stockDisponible unidades en stock.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            } else {
                Text(
                    text = "Selecciona una talla para ver el stock disponible.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.LocalShipping,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Envío gratis en pedidos superiores a $150",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.CheckCircleOutline,
                contentDescription = null,
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

        Button(
            onClick = { onEvent(CamisetaDetailEvent.AddToCart(camiseta.id)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryDark),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuantityDropdown(
    quantity: Int,
    maxStock: Int,
    isEnabled: Boolean,
    onQuantitySelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (isEnabled) expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = if (isEnabled) "Cantidad: $quantity" else "Cantidad: --",
            onValueChange = {},
            readOnly = true,
            enabled = isEnabled,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF1F5F9),
                unfocusedContainerColor = Color(0xFFF1F5F9),
                disabledContainerColor = Color(0xFFF1F5F9),
                focusedBorderColor = Color(0xFFE2E8F0),
                unfocusedBorderColor = Color(0xFFE2E8F0)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val limit = if (maxStock > 0) maxStock else 0
            if (limit > 0) {
                for (i in 1..limit) {
                    DropdownMenuItem(
                        text = { Text(text = "$i ${if (i == 1) "unidad" else "unidades"}") },
                        onClick = {
                            onQuantitySelected(i)
                            expanded = false
                        }
                    )
                }
            } else {
                DropdownMenuItem(
                    text = { Text(text = "Agotado") },
                    onClick = { expanded = false }
                )
            }
        }
    }
}

@Composable
fun SizeChip(
    size: String,
    isSelected: Boolean,
    isError: Boolean,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    val borderColor = when {
        !isEnabled -> Color(0xFFE2E8F0)
        isError -> MaterialTheme.colorScheme.error
        isSelected -> primaryDark
        else -> Color(0xFFE2E8F0)
    }

    val borderWidth = if (isError || isSelected) 2.dp else 1.dp
    
    val backgroundColor = when {
        !isEnabled -> Color(0xFFF1F5F9)
        isSelected -> Color(0xFFFFF7E6)
        else -> Color.White
    }

    val textColor = when {
        !isEnabled -> Color.LightGray
        isError -> MaterialTheme.colorScheme.error
        isSelected -> primaryDark
        else -> Color(0xFF07152B)
    }

    Box(
        modifier = Modifier
            .size(width = 64.dp, height = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = isEnabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = size,
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontSize = 14.sp
        )
    }
}