package com.example.golazo_store.presentation.favorites

import com.example.golazo_store.presentation.home.HomeScreen.ProductDemo

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val favoriteItems: List<ProductDemo> = emptyList(),
    val error: String? = null

)
