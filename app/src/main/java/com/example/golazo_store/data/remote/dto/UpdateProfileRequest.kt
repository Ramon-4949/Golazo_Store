package com.example.golazo_store.data.remote.dto

data class UpdateProfileRequest(
    val nombreUsuario: String,
    val nuevaContrasena: String? = null
)
