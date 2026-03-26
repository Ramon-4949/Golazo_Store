package com.example.golazo_store.presentation.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.golazo_store.presentation.home.ProductCard
import com.example.golazo_store.presentation.home.HomeEvent
import com.example.golazo_store.ui.theme.primaryDark

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    onBack: () -> Unit,
    bottomNavigation: @Composable () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    FavoritesBodyScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack,
        bottomNavigation = bottomNavigation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesBodyScreen(
    state: FavoritesUiState,
    onEvent: (FavoritesEvent) -> Unit,
    onBack: () -> Unit,
    bottomNavigation: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            FavoritesTopBar(onEvent = onEvent)
        },
        bottomBar = bottomNavigation
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${state.favoriteItems.size} artículos guardados",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onEvent(FavoritesEvent.ClickFilter) }
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtrar",
                        tint = primaryDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Filtrar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = primaryDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryDark)
                }
            } else if (state.favoriteItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay artículos guardados.", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.favoriteItems) { product ->
                        // Reusing ProductCard from presentation.home to maintain visual consistency
                        ProductCard(
                            product = product,
                            onEvent = { homeEvent ->
                                when (homeEvent) {
                                    is HomeEvent.ToggleFavorite -> onEvent(FavoritesEvent.RemoveFavorite(product.name))
                                    is HomeEvent.AddToCart -> onEvent(FavoritesEvent.AddToCart(product.name))
                                    else -> {}
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesTopBar(onEvent: (FavoritesEvent) -> Unit) {
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
        navigationIcon = {
            IconButton(onClick = { onEvent(FavoritesEvent.ClickMenu) }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
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
                IconButton(onClick = { onEvent(FavoritesEvent.ClickCart) }, modifier = Modifier.size(24.dp)) {
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
