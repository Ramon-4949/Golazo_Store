package com.example.golazo_store.domain.model

data class Categoria(
    val id: Int,
    val nombre: String,
    val camisetas: List<String> = emptyList()
)
