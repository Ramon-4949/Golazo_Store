package com.example.golazo_store.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.golazo_store.data.local.dao.CamisetaDao
import com.example.golazo_store.data.local.entity.CamisetaEntity
import com.example.golazo_store.data.local.dao.CartDao
import com.example.golazo_store.data.local.entity.CartEntity
import com.example.golazo_store.data.local.dao.FavoriteDao
import com.example.golazo_store.data.local.entity.FavoriteEntity

@Database(
    entities = [CamisetaEntity::class, CartEntity::class, FavoriteEntity::class],
    version = 8,
    exportSchema = false
)
abstract class GolazoDatabase : RoomDatabase() {
    abstract fun camisetaDao(): CamisetaDao
    abstract fun cartDao(): CartDao
    abstract fun favoriteDao(): FavoriteDao
}