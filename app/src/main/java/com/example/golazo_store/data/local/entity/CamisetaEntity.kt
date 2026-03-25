package com.example.golazo_store.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.golazo_store.domain.model.Camiseta

@Entity(tableName = "camisetas")
data class CamisetaEntity(
    @PrimaryKey
    val camisetaId: Int,
    val equipo: String,
    val liga: String,
    val temporada: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val imagenUrl: String
)

fun CamisetaEntity.toDomain(): Camiseta = Camiseta(
    camisetaId = camisetaId,
    equipo = equipo,
    liga = liga,
    temporada = temporada,
    descripcion = descripcion,
    precio = precio,
    stock = stock,
    imagenUrl = imagenUrl
)

fun Camiseta.toEntity(): CamisetaEntity = CamisetaEntity(
    camisetaId = camisetaId,
    equipo = equipo,
    liga = liga,
    temporada = temporada,
    descripcion = descripcion,
    precio = precio,
    stock = stock,
    imagenUrl = imagenUrl
)