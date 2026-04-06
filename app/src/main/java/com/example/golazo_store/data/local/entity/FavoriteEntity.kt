package com.example.golazo_store.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites", primaryKeys = ["usuarioId", "camisetaId"])
data class FavoriteEntity(
    val usuarioId: Int,
    val camisetaId: Int,
    var syncAction: String = "SYNCED"
)
