package com.example.golazo_store.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.golazo_store.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE usuarioId = :usuarioId AND camisetaId = :camisetaId")
    suspend fun deleteFavorite(usuarioId: Int, camisetaId: Int)

    @Query("UPDATE favorites SET syncAction = :syncAction WHERE usuarioId = :usuarioId AND camisetaId = :camisetaId")
    suspend fun updateSyncAction(usuarioId: Int, camisetaId: Int, syncAction: String)

    @Query("SELECT * FROM favorites WHERE usuarioId = :usuarioId AND camisetaId = :camisetaId")
    suspend fun getFavorite(usuarioId: Int, camisetaId: Int): FavoriteEntity?

    @Query("SELECT * FROM favorites WHERE usuarioId = :usuarioId")
    fun getFavoritosUsuario(usuarioId: Int): Flow<List<FavoriteEntity>>

    @Query("DELETE FROM favorites WHERE usuarioId = :usuarioId")
    suspend fun clearFavorites(usuarioId: Int)

    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()

    @Query("SELECT camisetaId FROM favorites WHERE usuarioId = :usuarioId AND syncAction != 'DELETED'")
    fun getAllFavoriteIds(usuarioId: Int): Flow<List<Int>>

    @Query("SELECT COUNT(*) FROM favorites WHERE usuarioId = :usuarioId AND camisetaId = :camisetaId AND syncAction != 'DELETED'")
    suspend fun isFavorite(usuarioId: Int, camisetaId: Int): Int

    @Query("SELECT * FROM favorites WHERE syncAction = 'ADDED'")
    suspend fun getPendingAdded(): List<FavoriteEntity>

    @Query("SELECT * FROM favorites WHERE syncAction = 'DELETED'")
    suspend fun getPendingDeleted(): List<FavoriteEntity>
}
