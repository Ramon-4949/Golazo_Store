package com.example.golazo_store.data.remote.dto

import com.example.golazo_store.domain.model.Direccion

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DireccionDto(
    @Json(name = "id") val id: Int? = 0,
    @Json(name = "usuarioId") val usuarioId: Int? = 0,
    @Json(name = "nombreDireccion") val nombreDireccion: String? = null,
    @Json(name = "calleNumero") val calleNumero: String? = null,
    @Json(name = "provincia") val provincia: String? = null,
    @Json(name = "codigoPostal") val codigoPostal: String? = null,
    @Json(name = "ciudad") val ciudad: String? = null,
    @Json(name = "reference") val reference: String? = null,
    @Json(name = "esPrincipal") val esPrincipal: Boolean? = false
) {
    fun toDomain() = Direccion(
        id = id ?: 0,
        usuarioId = usuarioId ?: 0,
        nombreDireccion = nombreDireccion.orEmpty(),
        calleNumero = calleNumero.orEmpty(),
        provincia = provincia.orEmpty(),
        codigoPostal = codigoPostal.orEmpty(),
        ciudad = ciudad.orEmpty(),
        reference = reference.orEmpty(),
        esPrincipal = esPrincipal ?: false
    )
}
