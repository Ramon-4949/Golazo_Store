package com.example.golazo_store.presentation.create

data class CreateUiState(
    val equipo: String = "",
    val liga: String = "",
    val temporada: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val stock: String = "",
    val imageUri: android.net.Uri? = null,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null

)
