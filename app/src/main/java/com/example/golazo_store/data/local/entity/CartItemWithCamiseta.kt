package com.example.golazo_store.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CartItemWithCamiseta(
    @Embedded val cartItem: CartEntity,
    @Relation(
        parentColumn = "camisetaId",
        entityColumn = "id"
    )
    val camiseta: CamisetaEntity
)
