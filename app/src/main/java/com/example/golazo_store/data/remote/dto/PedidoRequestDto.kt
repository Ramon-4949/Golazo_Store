package com.example.golazo_store.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PedidoRequestDto(
    val usuarioId: Int,
    val direccionId: Int,
    val metodoPagoId: Int,
    val items: List<DetallePedidoDto>
)

@JsonClass(generateAdapter = true)
data class DetallePedidoDto(
    val camisetaId: Int,
    val cantidad: Int,
    val talla: String
)
