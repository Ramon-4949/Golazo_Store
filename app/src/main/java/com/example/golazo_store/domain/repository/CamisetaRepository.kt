package com.example.golazo_store.domain.repository

import com.example.golazo_store.domain.model.Camiseta
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CamisetaRepository {
    fun getCamisetas(): Flow<Resource<List<Camiseta>>>
    fun getCamisetaById(id: Int): Flow<Resource<Camiseta>>
    suspend fun upsertCamiseta(camiseta: Camiseta): Resource<Unit>
    suspend fun deleteCamiseta(id: Int): Resource<Unit>
    suspend fun syncCamisetas(): Resource<Unit>
}