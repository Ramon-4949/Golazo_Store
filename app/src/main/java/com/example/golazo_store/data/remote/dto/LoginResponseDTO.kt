package com.example.golazo_store.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponseDTO(
    @Json(name = "mensaje") val mensaje: String?,
    @Json(name = "usuario") val usuario: UsuarioDTO?
)

@JsonClass(generateAdapter = true)
data class UsuarioDTO(
    @Json(name = "id") val id: Int?,
    @Json(name = "nombre") val nombre: String?,
    @Json(name = "correo") val correo: String?,
    @Json(name = "rol") val rol: String?
) {
    fun toDomain(): com.example.golazo_store.domain.model.Usuario {
        return com.example.golazo_store.domain.model.Usuario(
            id = id ?: 0,
            nombre = nombre ?: "",
            correo = correo ?: "",
            rol = rol ?: "Cliente"
        )
    }
}
