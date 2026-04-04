package com.example.golazo_store.presentation.cart

import com.example.golazo_store.domain.model.CartItem

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val subtotal: Double = 0.0,
    val total: Double = 0.0,
    val errorMessage: String? = null
)
