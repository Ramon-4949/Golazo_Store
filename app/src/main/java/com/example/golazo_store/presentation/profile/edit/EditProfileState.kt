package com.example.golazo_store.presentation.profile.edit

data class EditProfileState(
    val id: Int = 0,
    val nombreUsuario: String = "",
    val nuevaContrasena: String = "",
    val confirmarContrasena: String = "",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)
