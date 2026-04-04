package com.example.golazo_store.presentation.address.list

import com.example.golazo_store.domain.model.Direccion

data class AddressListUiState(
    val addresses: List<Direccion> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = ""
)
