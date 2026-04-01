package com.example.golazo_store.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.golazo_store.domain.model.Camiseta

@Entity(tableName = "camisetas")
data class CamisetaEntity(
    @PrimaryKey
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
    val stockTotal: Int,
    val categoriaId: Int
)

fun CamisetaEntity.toDomain(): Camiseta = Camiseta(
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
    categoria = null
)

fun Camiseta.toEntity(): CamisetaEntity = CamisetaEntity(
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
    categoriaId = categoriaId
)