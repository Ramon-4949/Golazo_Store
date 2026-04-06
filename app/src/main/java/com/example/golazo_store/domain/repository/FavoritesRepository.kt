package com.example.golazo_store.domain.repository

import com.example.golazo_store.domain.model.Camiseta
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavoriteCamisetas(): Flow<Resource<List<Camiseta>>>
    fun getFavoriteIds(): Flow<List<Int>>
    suspend fun toggleFavorite(camisetaId: Int)
    suspend fun syncFavorites(): Resource<Unit>
    suspend fun syncDownFavorites()
    suspend fun clearLocalFavorites()
    suspend fun clearAllFavorites()
}
