package com.example.golazo_store.data.remote.dto

import com.example.golazo_store.domain.model.Camiseta

data class CamisetaDto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenUrl: String,
    val stockS: Int? = 0,
    val stockM: Int? = 0,
    val stockL: Int? = 0,
    val stockXL: Int? = 0,
    val stock2XL: Int? = 0,
    val stockTotal: Int? = 0,
    val categoriaId: Int? = 0,
    val categoria: String? = null
) {
    fun toDomain() = Camiseta(
        id = id,
        nombre = nombre,
        descripcion = descripcion,
        precio = precio,
        imagenUrl = imagenUrl,
        stockS = stockS ?: 0,
        stockM = stockM ?: 0,
        stockL = stockL ?: 0,
        stockXL = stockXL ?: 0,
        stock2XL = stock2XL ?: 0,
        stockTotal = stockTotal ?: 0,
        categoriaId = categoriaId ?: 0,
        categoria = null
    )
}