package com.example.golazo_store.presentation.home

import com.example.golazo_store.domain.model.Camiseta

data class HomeUiState(
    val isLoading: Boolean = false,
    val allProducts: List<Camiseta> = emptyList(),
    val products: List<Camiseta> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val selectedFilter: String = "Todo",
    val filters: List<String> = listOf("Todo", "Clubes", "Selecciones", "Entrenamiento"),
    val categoryMap: Map<Int, String> = emptyMap(),
    val cartItemCount: Int = 0,
    val favoriteIds: Set<Int> = emptySet()
)
