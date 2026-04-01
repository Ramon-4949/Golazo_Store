package com.example.golazo_store.presentation.gestion

import com.example.golazo_store.domain.model.Camiseta

data class GestionUiState(
    val camisetas: List<Camiseta> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
