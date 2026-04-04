package com.example.golazo_store.presentation.create.list

import com.example.golazo_store.domain.model.Camiseta

data class CreateListUiState(
    val camisetas: List<Camiseta> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)


