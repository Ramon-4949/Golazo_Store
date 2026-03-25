package com.example.golazo_store.data.remote.dto

import com.example.golazo_store.domain.model.Camiseta

data class CamisetaDto(
    val camisetaId: Int,
    val equipo: String,
    val liga: String,
    val temporada: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val imagenUrl: String
) {
    fun toDomain() = Camiseta(
        camisetaId = camisetaId,
        equipo = equipo,
        liga = liga,
        temporada = temporada,
        descripcion = descripcion,
        precio = precio,
        stock = stock,
        imagenUrl = imagenUrl
    )
}