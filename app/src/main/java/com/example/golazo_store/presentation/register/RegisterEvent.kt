package com.example.golazo_store.presentation.register

sealed interface RegisterEvent {
    data class NombreChanged(val nombre: String) : RegisterEvent
    data class CorreoChanged(val correo: String) : RegisterEvent
    data class ContrasenaChanged(val contrasena: String) : RegisterEvent
    data class ConfirmContrasenaChanged(val confirmContrasena: String) : RegisterEvent
    object RegisterClicked : RegisterEvent
}

