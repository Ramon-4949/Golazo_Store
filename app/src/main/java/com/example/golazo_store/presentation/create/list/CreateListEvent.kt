package com.example.golazo_store.presentation.create.list

sealed class CreateListEvent {
    data class OnDeleteClick(val camiseta: com.example.golazo_store.domain.model.Camiseta) : CreateListEvent()
}




