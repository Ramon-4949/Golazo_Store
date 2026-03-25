package com.example.golazo_store.domain.model

data class Camiseta(
    val camisetaId: Int = 0,
    val equipo: String,
    val liga: String,
    val temporada: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val imagenUrl: String
)