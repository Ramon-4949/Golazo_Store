package com.example.golazo_store.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.golazo_store.data.local.dao.CamisetaDao
import com.example.golazo_store.data.local.entity.CamisetaEntity

@Database(
    entities = [CamisetaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GolazoDatabase : RoomDatabase() {
    abstract fun camisetaDao(): CamisetaDao
}