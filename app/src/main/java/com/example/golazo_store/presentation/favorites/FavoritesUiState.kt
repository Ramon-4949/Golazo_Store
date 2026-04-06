package com.example.golazo_store.presentation.favorites

import com.example.golazo_store.domain.model.Camiseta

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val favoriteItems: List<Camiseta> = emptyList(),
    val cartItemCount: Int = 0,
    val categoryMap: Map<Int, String> = emptyMap(),
    val error: String? = null
)

