package com.example.golazo_store.domain.model

data class MetodoPagoRegistro(
    val titular: String,
    val numeroTarjeta: String,
    val expiracion: String,
    val cvv: String,
    val esPrincipal: Boolean
) {
    fun toDto(usuarioId: Int): com.example.golazo_store.data.remote.dto.MetodoPagoRequestDto {
        return com.example.golazo_store.data.remote.dto.MetodoPagoRequestDto(
            usuarioId = usuarioId,
            titular = titular,
            numeroTarjeta = numeroTarjeta,
            expiracion = expiracion,
            cvv = cvv,
            esPrincipal = esPrincipal
        )
    }
}
