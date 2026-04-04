package com.example.golazo_store.presentation.home

import com.example.golazo_store.domain.model.Camiseta

data class HomeUiState(
    val isLoading: Boolean = false,
    val products: List<Camiseta> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val selectedFilter: String = "Todo",
    val filters: List<String> = listOf("Todo", "Precio", "Talla", "Retro"),
    val cartItemCount: Int = 0
)
