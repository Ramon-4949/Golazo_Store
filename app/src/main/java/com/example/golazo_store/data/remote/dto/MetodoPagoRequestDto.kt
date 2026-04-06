package com.example.golazo_store.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MetodoPagoRequestDto(
    val usuarioId: Int,
    val titular: String,
    val numeroTarjeta: String,
    val expiracion: String,
    val cvv: String,
    val esPrincipal: Boolean
)
