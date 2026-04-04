package com.example.golazo_store.presentation.address.edit

data class AddressEditUiState(
    val id: Int? = null,
    val nombreDireccion: String = "",
    val calleNumero: String = "",
    val provincia: String = "",
    val codigoPostal: String = "",
    val ciudad: String = "",
    val reference: String = "",
    val esPrincipal: Boolean = false,
    
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)
