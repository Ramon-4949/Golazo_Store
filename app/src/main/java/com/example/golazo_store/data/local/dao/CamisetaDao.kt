package com.example.golazo_store.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.golazo_store.data.local.entity.CamisetaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CamisetaDao {
    @Upsert
    suspend fun upsert(entity: CamisetaEntity)

    @Delete
    suspend fun delete(entity: CamisetaEntity)

    @Query("SELECT * FROM camisetas ORDER BY camisetaId DESC")
    fun observeAll(): Flow<List<CamisetaEntity>>

    @Query("SELECT * FROM camisetas WHERE camisetaId = :id")
    suspend fun getById(id: Int): CamisetaEntity?
}