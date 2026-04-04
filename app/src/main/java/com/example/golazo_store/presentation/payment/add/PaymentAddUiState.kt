package com.example.golazo_store.presentation.payment.add

data class PaymentAddUiState(
    val nombreTitular: String = "",
    val numeroTarjeta: String = "",
    val expiracionMMYY: String = "", // Used in form "12/25"
    val cvv: String = "",
    val esPrincipal: Boolean = false,
    
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)
