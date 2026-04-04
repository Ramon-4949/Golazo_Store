package com.example.golazo_store.data.remote.remotedatasource

import com.example.golazo_store.data.remote.api.GolazoApi
import com.example.golazo_store.data.remote.dto.MetodoPagoDto
import com.example.golazo_store.data.remote.dto.MetodoPagoRequestDto
import javax.inject.Inject

class MetodoPagoRemoteDataSource @Inject constructor(
    private val api: GolazoApi
) {
    suspend fun getMetodosPago(): Result<List<MetodoPagoDto>> {
        return try {
            val response = api.getMetodosPago()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener métodos de pago: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createMetodoPago(request: MetodoPagoRequestDto): Result<MetodoPagoDto> {
        return try {
            val response = api.createMetodoPago(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Respuesta vacía al crear tarjeta"))
            } else {
                Result.failure(Exception("Error al crear tarjeta: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMetodoPago(id: Int): Result<Unit> {
        return try {
            val response = api.deleteMetodoPago(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar tarjeta: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
