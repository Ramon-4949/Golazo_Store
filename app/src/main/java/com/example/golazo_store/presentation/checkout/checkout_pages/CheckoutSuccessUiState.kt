package com.example.golazo_store.presentation.checkout.checkout_pages

import com.example.golazo_store.domain.model.PedidoAdmin

data class CheckoutSuccessUiState(
    val isLoading: Boolean = true,
    val orderNumber: String = "",
    val pedido: PedidoAdmin? = null,
    val error: String? = null
)
