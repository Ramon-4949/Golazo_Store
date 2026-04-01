package com.example.golazo_store.data.remote.dto

import com.example.golazo_store.domain.model.Camiseta

data class CamisetaDto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenUrl: String,
    val stockS: Int,
    val stockM: Int,
    val stockL: Int,
    val stockXL: Int,
    val stock2XL: Int,
    val stockTotal: Int = 0,
    val categoriaId: Int,
    val categoria: CategoriaDto? = null
) {
    fun toDomain() = Camiseta(
        id = id,
        nombre = nombre,
        descripcion = descripcion,
        precio = precio,
        imagenUrl = imagenUrl,
        stockS = stockS,
        stockM = stockM,
        stockL = stockL,
        stockXL = stockXL,
        stock2XL = stock2XL,
        stockTotal = stockTotal,
        categoriaId = categoriaId,
        categoria = categoria?.toDomain()
    )
}