package com.example.golazo_store.presentation.home

import com.example.golazo_store.presentation.home.HomeScreen.ProductDemo

data class HomeUiState(
    val isLoading: Boolean = false,
    val products: List<ProductDemo> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val selectedFilter: String = "Todo",
    val filters: List<String> = listOf("Todo", "Precio", "Talla", "Retro")
)
