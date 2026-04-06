package com.example.golazo_store.presentation.checkout.checkout_pages

import com.example.golazo_store.domain.model.CartItem
import com.example.golazo_store.domain.model.Direccion
import com.example.golazo_store.domain.model.MetodoPago

data class CheckoutUiState(
    val cartItems: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val total: Double = 0.0,
    val direccionPrincipal: Direccion? = null,
    val metodoPagoPrincipal: MetodoPago? = null,
    val allDirecciones: List<Direccion> = emptyList(),
    val allMetodosPago: List<MetodoPago> = emptyList(),
    val isLoadingData: Boolean = true,
    val isPlacingOrder: Boolean = false,
    val error: String? = null,
    val createdOrderNumber: String? = null
)
