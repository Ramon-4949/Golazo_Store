package com.example.golazo_store.domain.model

import com.example.golazo_store.data.remote.dto.DireccionDto

data class Direccion(
    val id: Int,
    val usuarioId: Int,
    val nombreDireccion: String,
    val calleNumero: String,
    val provincia: String,
    val codigoPostal: String,
    val ciudad: String,
    val reference: String,
    val esPrincipal: Boolean
) {
    fun toDto() = DireccionDto(
        id = id,
        usuarioId = usuarioId,
        nombreDireccion = nombreDireccion,
        calleNumero = calleNumero,
        provincia = provincia,
        codigoPostal = codigoPostal,
        ciudad = ciudad,
        reference = reference,
        esPrincipal = esPrincipal
    )
}
