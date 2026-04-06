package com.example.golazo_store.data.remote.remotedatasource

import com.example.golazo_store.data.remote.api.FavoritoApiService
import com.example.golazo_store.data.remote.dto.FavoritoDto
import com.example.golazo_store.domain.utils.Resource
import javax.inject.Inject

class FavoritoRemoteDataSource @Inject constructor(
    private val apiService: FavoritoApiService
) {
    suspend fun addFavorite(favoritoDto: FavoritoDto): Resource<Unit> {
        return try {
            val response = apiService.addFavorite(favoritoDto)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Error al agregar favorito: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun deleteFavorite(usuarioId: Int, camisetaId: Int): Resource<Unit> {
        return try {
            val response = apiService.deleteFavorite(usuarioId, camisetaId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Error al eliminar favorito: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }

    suspend fun getMisFavoritos(usuarioId: Int): Resource<List<FavoritoDto>> {
        return try {
            val response = apiService.getMisFavoritos(usuarioId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Cuerpo de respuesta vacío")
            } else {
                Resource.Error("Error al obtener favoritos: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de red")
        }
    }
}
