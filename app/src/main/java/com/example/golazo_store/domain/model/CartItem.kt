package com.example.golazo_store.domain.model

data class CartItem(
    val cartId: Int,
    val camisetaId: Int,
    val nombre: String,
    val precio: Double,
    val imagenUrl: String,
    val talla: String,
    val cantidad: Int
)
