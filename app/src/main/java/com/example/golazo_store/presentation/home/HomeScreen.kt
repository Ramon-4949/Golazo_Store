package com.example.golazo_store.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.golazo_store.domain.model.Camiseta
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToCart: () -> Unit,
    bottomNavigation: @Composable () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeBodyScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToCart = onNavigateToCart,
        bottomNavigation = bottomNavigation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBodyScreen(
    state: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToCart: () -> Unit,
    bottomNavigation: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                cartItemCount = state.cartItemCount,
                onEvent = onEvent, 
                onNavigateToCart = onNavigateToCart
            )
        },
        bottomBar = bottomNavigation
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { onEvent(HomeEvent.UpdateSearch(it)) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            FilterChipsRow(
                filters = state.filters,
                selectedFilter = state.selectedFilter,
                onFilterSelected = { onEvent(HomeEvent.SelectFilter(it)) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.error, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }
            } else {
                ProductsGrid(
                    products = state.products,
                    categoryMap = state.categoryMap,
                    favoriteIds = state.favoriteIds,
                    onEvent = onEvent,
                    onNavigateToDetail = onNavigateToDetail
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    cartItemCount: Int,
    onEvent: (HomeEvent) -> Unit,
    onNavigateToCart: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "GOLAZO STORE",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        actions = {
            IconButton(
                onClick = { onNavigateToCart() },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text(text = cartItemCount.toString(), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Buscar camisetas, equipos...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun FilterChipsRow(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            val isSelected = filter == selectedFilter

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = filter,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ProductsGrid(
    products: List<Camiseta>,
    categoryMap: Map<Int, String>,
    favoriteIds: Set<Int>,
    onEvent: (HomeEvent) -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
    format.maximumFractionDigits = 0

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                categoryMap = categoryMap,
                format = format,
                isFavorite = product.id in favoriteIds,
                onEvent = onEvent,
                onNavigateToDetail = onNavigateToDetail
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Camiseta,
    categoryMap: Map<Int, String>,
    format: NumberFormat,
    isFavorite: Boolean,
    onEvent: (HomeEvent) -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.clickable { onNavigateToDetail(product.id) }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                val imageUrl = product.imagenUrl?.takeIf { it.isNotBlank() }
                if (imageUrl != null) {
                    val fullUrl = if (imageUrl.startsWith("http")) imageUrl else "http://golazostoreapi.somee.com\$imageUrl"
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(fullUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = product.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(0.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "No image",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .clickable { onEvent(HomeEvent.ToggleFavorite(product.id)) },
                    contentAlignment = Alignment.Center
                ) {
                    val icon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.Favorite
                    val iconTint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    Icon(
                        imageVector = icon,
                        contentDescription = "Favorite",
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val rawCategoryName = categoryMap[product.categoriaId] ?: "OTROS"
                val categoryName = rawCategoryName.uppercase()

                Text(
                    text = categoryName,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = product.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = format.format(product.precio),
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
