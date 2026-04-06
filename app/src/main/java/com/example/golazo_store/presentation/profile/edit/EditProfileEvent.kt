package com.example.golazo_store.presentation.profile.edit

sealed class EditProfileEvent {
    data class OnNombreUsuarioChange(val nombre: String) : EditProfileEvent()
    data class OnNuevaContrasenaChange(val contrasena: String) : EditProfileEvent()
    data class OnConfirmarContrasenaChange(val contrasena: String) : EditProfileEvent()
    object OnGuardarCambios : EditProfileEvent()
    object OnEliminarCuenta : EditProfileEvent()
    object OnDismissError : EditProfileEvent()
    object OnDismissSuccess : EditProfileEvent()
}
