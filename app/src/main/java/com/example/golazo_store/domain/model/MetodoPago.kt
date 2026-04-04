package com.example.golazo_store.domain.model

data class MetodoPago(
    val id: Int,
    val usuarioId: Int,
    val tipoTarjeta: String,
    val titular: String,
    val numeroOculto: String,
    val mesExpiracion: String,
    val anioExpiracion: String,
    val esPrincipal: Boolean
) {
    fun toDto(): com.example.golazo_store.data.remote.dto.MetodoPagoDto {
        return com.example.golazo_store.data.remote.dto.MetodoPagoDto(
            id = id,
            usuarioId = usuarioId,
            tipoTarjeta = tipoTarjeta,
            titular = titular,
            numeroOculto = numeroOculto,
            mesExpiracion = mesExpiracion,
            anioExpiracion = anioExpiracion,
            esPrincipal = esPrincipal
        )
    }
}
