package com.example.golazo_store.presentation.favorites

import com.example.golazo_store.domain.model.Camiseta

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val favoriteItems: List<Camiseta> = emptyList(),
    val error: String? = null
)

