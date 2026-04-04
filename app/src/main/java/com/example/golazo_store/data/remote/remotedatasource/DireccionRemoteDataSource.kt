package com.example.golazo_store.data.remote.remotedatasource

import com.example.golazo_store.data.remote.api.GolazoApi
import com.example.golazo_store.data.remote.dto.DireccionDto
import javax.inject.Inject

class DireccionRemoteDataSource @Inject constructor(
    private val api: GolazoApi
) {
    suspend fun getDirecciones(): Result<List<DireccionDto>> {
        return try {
            val response = api.getDirecciones()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener las direcciones: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDireccionById(id: Int): Result<DireccionDto> {
        return try {
            val response = api.getDirecciones()
            if (response.isSuccessful) {
                val list = response.body() ?: emptyList()
                val match = list.find { it.id == id }
                if (match != null) {
                    Result.success(match)
                } else {
                    Result.failure(Exception("Dirección no encontrada"))
                }
            } else {
                Result.failure(Exception("Error al buscar la dirección: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createDireccion(direccion: DireccionDto): Result<DireccionDto> {
        return try {
            val response = api.createDireccion(direccion)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Respuesta vacía al crear dirección"))
            } else {
                Result.failure(Exception("Error al crear la dirección: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateDireccion(id: Int, direccion: DireccionDto): Result<Unit> {
        return try {
            val response = api.updateDireccion(id, direccion)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al actualizar la dirección: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteDireccion(id: Int): Result<Unit> {
        return try {
            val response = api.deleteDireccion(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar la dirección: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
