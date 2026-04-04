package com.example.golazo_store.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PedidoDto(
    val id: Int? = null,
    val numeroPedido: String? = null,
    val estado: String? = null,
    val fechaPedido: String? = null,
    val total: Double? = null
)
