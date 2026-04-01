package com.example.golazo_store.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.golazo_store.ui.theme.primaryDark
import com.example.golazo_store.ui.theme.primaryLight

object HomeScreen {
    // Nested data class to keep the import scope clean while avoiding breaking previous references
    data class ProductDemo(
        val name: String,
        val price: String,
        val isFavorite: Boolean,
        val backgroundColor: Color
    )
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCreate: () -> Unit,
    bottomNavigation: @Composable () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeBodyScreen(
        state = state,
        onEvent = viewModel::onEvent,
        bottomNavigation = bottomNavigation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBodyScreen(
    state: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    bottomNavigation: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            HomeTopBar(onEvent = onEvent)
        },
        bottomBar = bottomNavigation
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
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
                    CircularProgressIndicator(color = primaryDark)
                }
            } else {
                ProductsGrid(
                    products = state.products,
                    onEvent = onEvent
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(onEvent: (HomeEvent) -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "GOLAZO STORE",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },

        actions = {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = primaryDark,
                        contentColor = Color.Black
                    ) {
                        Text(text = "2", fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.padding(end = 16.dp)
            ) {
                IconButton(onClick = { onEvent(HomeEvent.ClickCart) }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
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
        placeholder = { Text("Buscar camisetas, equipos...", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedContainerColor = Color(0xFFF0F4F8),
            focusedContainerColor = Color(0xFFF0F4F8)
        )
    )
}

@Composable
fun FilterChipsRow(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selectedFilter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) primaryDark else Color(0xFFF0F4F8))
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = filter,
                    color = if (isSelected) Color.Black else Color.DarkGray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ProductsGrid(
    products: List<HomeScreen.ProductDemo>,
    onEvent: (HomeEvent) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductCard(product, onEvent)
        }
    }
}

@Composable
fun ProductCard(
    product: HomeScreen.ProductDemo,
    onEvent: (HomeEvent) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(product.backgroundColor)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Imagen de\n${product.name}", color = Color.White, textAlign = TextAlign.Center)
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                        .clickable { onEvent(HomeEvent.ToggleFavorite(product.name)) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (product.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (product.isFavorite) primaryDark else Color.DarkGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.price,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onEvent(HomeEvent.AddToCart(product.name)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryDark),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Add",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Añadir", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}


