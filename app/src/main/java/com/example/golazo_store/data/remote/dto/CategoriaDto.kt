package com.example.golazo_store.data.remote.dto

import com.example.golazo_store.domain.model.Categoria

data class CategoriaDto(
    val id: Int,
    val nombre: String,
    val camisetas: Any? = null
) {
    fun toDomain() = Categoria(
        id = id,
        nombre = nombre,
        camisetas = emptyList()
    )
}
