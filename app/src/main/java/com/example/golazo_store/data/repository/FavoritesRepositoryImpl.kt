package com.example.golazo_store.data.repository

import com.example.golazo_store.data.local.dao.FavoriteDao
import com.example.golazo_store.data.local.entity.FavoriteEntity
import com.example.golazo_store.domain.model.Camiseta
import com.example.golazo_store.domain.repository.CamisetaRepository
import com.example.golazo_store.domain.repository.FavoritesRepository
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

import com.example.golazo_store.data.remote.remotedatasource.FavoritoRemoteDataSource
import com.example.golazo_store.data.remote.dto.FavoritoDto
import com.example.golazo_store.data.local.SessionManager

class FavoritesRepositoryImpl(
    private val favoriteDao: FavoriteDao,
    private val camisetaRepository: CamisetaRepository,
    private val sessionManager: SessionManager,
    private val remotoDataSource: FavoritoRemoteDataSource
) : FavoritesRepository {

    private val currentUserId: Int
        get() = sessionManager.getUserSession()?.id ?: 0

    override fun getFavoriteCamisetas(): Flow<Resource<List<Camiseta>>> {
        return combine(
            camisetaRepository.getCamisetas(),
            favoriteDao.getAllFavoriteIds(currentUserId)
        ) { resource, favoriteIds ->
            when (resource) {
                is Resource.Success -> {
                    val filtered = resource.data?.filter { it.id in favoriteIds } ?: emptyList()
                    Resource.Success(filtered)
                }
                is Resource.Error -> Resource.Error(resource.message ?: "Unknown error")
                is Resource.Loading -> Resource.Loading()
            }
        }
    }

    override fun getFavoriteIds(): Flow<List<Int>> {
        return favoriteDao.getAllFavoriteIds(currentUserId)
    }

    override suspend fun toggleFavorite(camisetaId: Int) {
        val uid = currentUserId
        if (uid == 0) return

        val existing = favoriteDao.getFavorite(uid, camisetaId)
        var newAction = ""

        if (existing != null) {
            when (existing.syncAction) {
                "ADDED" -> {
                    favoriteDao.deleteFavorite(uid, camisetaId)
                    newAction = "DELETE_PERMANENT"
                }
                "SYNCED" -> {
                    favoriteDao.updateSyncAction(uid, camisetaId, "DELETED")
                    newAction = "DELETED"
                }
                "DELETED" -> {
                    favoriteDao.updateSyncAction(uid, camisetaId, "ADDED")
                    newAction = "ADDED"
                }
            }
        } else {
            favoriteDao.insertFavorite(FavoriteEntity(uid, camisetaId, "ADDED"))
            newAction = "ADDED"
        }

        // --- Immediate Sync Up ---
        try {
            if (newAction == "ADDED") {
                val result = remotoDataSource.addFavorite(FavoritoDto(uid, camisetaId))
                if (result is Resource.Success) {
                    favoriteDao.updateSyncAction(uid, camisetaId, "SYNCED")
                }
            } else if (newAction == "DELETED" || newAction == "DELETE_PERMANENT") {
                val result = remotoDataSource.deleteFavorite(uid, camisetaId)
                if (result is Resource.Success) {
                    favoriteDao.deleteFavorite(uid, camisetaId)
                }
            }
        } catch (e: Exception) {
            // En caso de fallo de red, se queda marcado como ADDED/DELETED 
            // para que un futuro SyncWorker lo retome. (Offline-First Real)
        }
    }

    override suspend fun syncFavorites(): Resource<Unit> {
        val pendingAdded = favoriteDao.getPendingAdded()
        val pendingDeleted = favoriteDao.getPendingDeleted()

        var success = true

        for (fav in pendingAdded) {
            val result = remotoDataSource.addFavorite(FavoritoDto(fav.usuarioId, fav.camisetaId))
            if (result is Resource.Success) {
                favoriteDao.updateSyncAction(fav.usuarioId, fav.camisetaId, "SYNCED")
            } else {
                success = false
            }
        }

        for (fav in pendingDeleted) {
            val result = remotoDataSource.deleteFavorite(fav.usuarioId, fav.camisetaId)
            if (result is Resource.Success) {
                favoriteDao.deleteFavorite(fav.usuarioId, fav.camisetaId)
            } else {
                // Ignore 404s if the backend already deleted it, but wait, error message might be difficult to parse, let WorkManager retry
                success = false
            }
        }

        return if (success) Resource.Success(Unit) else Resource.Error("Algunas sincronizaciones fallaron")
    }

    override suspend fun syncDownFavorites() {
        val uid = currentUserId
        if (uid == 0) return
        val result = remotoDataSource.getMisFavoritos(uid)
        if (result is Resource.Success) {
            val favs = result.data ?: emptyList()
            favs.forEach { fav ->
                val entity = FavoriteEntity(
                    usuarioId = uid,
                    camisetaId = fav.camisetaId ?: 0,
                    syncAction = "SYNCED"
                )
                favoriteDao.insertFavorite(entity)
            }
        }
    }

    override suspend fun clearLocalFavorites() {
        val uid = currentUserId
        if (uid > 0) favoriteDao.clearFavorites(uid)
    }

    override suspend fun clearAllFavorites() {
        favoriteDao.deleteAllFavorites()
    }
}
