package com.example.golazo_store.domain.model

data class Camiseta(
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenUrl: String,
    val stockS: Int = 0,
    val stockM: Int = 0,
    val stockL: Int = 0,
    val stockXL: Int = 0,
    val stock2XL: Int = 0,
    val stockTotal: Int = 0,
    val categoriaId: Int = 0,
    val categoria: Categoria? = null
)