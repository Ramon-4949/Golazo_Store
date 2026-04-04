package com.example.golazo_store.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MetodoPagoDto(
    val id: Int? = 0,
    val usuarioId: Int? = 0,
    val tipoTarjeta: String? = "",
    val titular: String? = "",
    val numeroOculto: String? = "",
    val mesExpiracion: String? = "",
    val anioExpiracion: String? = "",
    val esPrincipal: Boolean? = false
) {
    fun toDomain(): com.example.golazo_store.domain.model.MetodoPago {
        return com.example.golazo_store.domain.model.MetodoPago(
            id = id ?: 0,
            usuarioId = usuarioId ?: 0,
            tipoTarjeta = tipoTarjeta ?: "",
            titular = titular ?: "",
            numeroOculto = numeroOculto ?: "",
            mesExpiracion = mesExpiracion ?: "",
            anioExpiracion = anioExpiracion ?: "",
            esPrincipal = esPrincipal ?: false
        )
    }
}
