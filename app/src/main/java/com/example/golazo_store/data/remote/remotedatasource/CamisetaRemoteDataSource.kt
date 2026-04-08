package com.example.golazo_store.data.remote.remotedatasource

import com.example.golazo_store.data.remote.api.GolazoApi
import com.example.golazo_store.data.remote.dto.CamisetaDto
import javax.inject.Inject

class CamisetaRemoteDataSource @Inject constructor(
    private val api: GolazoApi
) {
    suspend fun getCamisetas(): Result<List<CamisetaDto>> {
        return try {
            val response = api.getCamisetas()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error de red ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error desconocido", e))
        }
    }

    suspend fun getCamisetaById(id: Int): Result<CamisetaDto> {
        return try {
            val response = api.getCamisetaById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error de red ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error desconocido: ${e.message}", e))
        }
    }


    suspend fun createCamiseta(camiseta: CamisetaDto): Result<CamisetaDto> {
        return try {
            val response = api.createCamiseta(camiseta)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error desconocido", e))
        }
    }

    suspend fun updateCamiseta(id: Int, camiseta: CamisetaDto): Result<Unit> {
        return try {
            val response = api.updateCamiseta(id, camiseta)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al actualizar ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error desconocido", e))
        }
    }

    suspend fun deleteCamiseta(id: Int): Result<Unit> {
        return try {
            val response = api.deleteCamiseta(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error desconocido", e))
        }
    }
}