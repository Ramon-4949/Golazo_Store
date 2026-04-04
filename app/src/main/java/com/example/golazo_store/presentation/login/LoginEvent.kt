package com.example.golazo_store.presentation.login

sealed interface LoginEvent {
    data class CorreoChanged(val correo: String) : LoginEvent
    data class ContrasenaChanged(val contrasena: String) : LoginEvent
    object LoginClicked : LoginEvent
}

