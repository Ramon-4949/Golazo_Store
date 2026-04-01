package com.example.golazo_store.presentation.gestion

sealed class GestionEvent {
    data class OnDeleteClick(val camiseta: com.example.golazo_store.domain.model.Camiseta) : GestionEvent()
}
