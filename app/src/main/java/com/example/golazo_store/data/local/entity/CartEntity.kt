package com.example.golazo_store.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = CamisetaEntity::class,
            parentColumns = ["id"],
            childColumns = ["camisetaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("camisetaId")]
)
data class CartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val camisetaId: Int,
    val talla: String,
    val cantidad: Int
)
