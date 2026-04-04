package com.example.golazo_store.presentation.detail

import com.example.golazo_store.domain.model.Camiseta

data class CamisetaDetailUiState(
    val isLoading: Boolean = false,
    val camiseta: Camiseta? = null,
    val selectedSize: String? = null,
    val errorMessage: String? = null
)
