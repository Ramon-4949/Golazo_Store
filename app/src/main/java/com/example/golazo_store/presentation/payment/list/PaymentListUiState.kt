package com.example.golazo_store.presentation.payment.list

import com.example.golazo_store.domain.model.MetodoPago

data class PaymentListUiState(
    val payments: List<MetodoPago> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
