package com.example.golazo_store.presentation.create

sealed interface CreateEvent {
    data class OnEquipoChange(val equipo: String) : CreateEvent
    data class OnLigaChange(val liga: String) : CreateEvent
    data class OnTemporadaChange(val temporada: String) : CreateEvent
    data class OnDescripcionChange(val descripcion: String) : CreateEvent
    data class OnPrecioChange(val precio: String) : CreateEvent
    data class OnStockChange(val stock: String) : CreateEvent
    data class OnImagePicked(val uri: android.net.Uri?) : CreateEvent
    object SaveProduct : CreateEvent
    object ClearMessages : CreateEvent

}
