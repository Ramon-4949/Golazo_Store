package com.example.golazo_store.presentation.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel = hiltViewModel(),
    bottomNavigation: @Composable () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CategoriesBodyScreen(
        state = state,
        onEvent = viewModel::onEvent,
        bottomNavigation = bottomNavigation
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesBodyScreen(
    state: CategoriesUiState,
    onEvent: (CategoriesEvent) -> Unit,
    bottomNavigation: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            CategoriesTopBar(
                cartItemCount = state.cartItemCount,
                onEvent = onEvent
            )
        },
        bottomBar = bottomNavigation
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                CategoriesGrid(
                    categories = state.categories,
                    onEvent = onEvent
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesTopBar(
    cartItemCount: Int,
    onEvent: (CategoriesEvent) -> Unit
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
                onClick = { onEvent(CategoriesEvent.ClickCart) },
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
fun CategoriesGrid(
    categories: List<CategoryDemo>,
    onEvent: (CategoriesEvent) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explorar Categorías",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        
        items(
            items = categories,
            span = { category ->
                if (category.title == "Clubes") GridItemSpan(maxLineSpan) else GridItemSpan(1)
            }
        ) { category ->
            CategoryCard(category = category, onEvent = onEvent)
        }
    }
}

@Composable
fun CategoryCard(
    category: CategoryDemo,
    onEvent: (CategoriesEvent) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onEvent(CategoriesEvent.ClickCategory(category.title)) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            coil.compose.AsyncImage(
                model = category.imageResId,
                contentDescription = category.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f))
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    if (category.badge != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = category.badge,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Text(
                        text = category.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        text = category.subtitle,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}



