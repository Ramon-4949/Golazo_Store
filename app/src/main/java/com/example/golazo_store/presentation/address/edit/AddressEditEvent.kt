package com.example.golazo_store.presentation.address.edit

sealed class AddressEditEvent {
    data class OnNombreDireccionChange(val value: String) : AddressEditEvent()
    data class OnCalleNumeroChange(val value: String) : AddressEditEvent()
    data class OnProvinciaChange(val value: String) : AddressEditEvent()
    data class OnCodigoPostalChange(val value: String) : AddressEditEvent()
    data class OnCiudadChange(val value: String) : AddressEditEvent()
    data class OnReferenceChange(val value: String) : AddressEditEvent()
    data class OnEsPrincipalChange(val value: Boolean) : AddressEditEvent()
    object OnSave : AddressEditEvent()
}
