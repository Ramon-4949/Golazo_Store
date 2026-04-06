package com.example.golazo_store.data.remote.remotedatasource

import com.example.golazo_store.data.remote.api.GolazoApi
import com.example.golazo_store.data.remote.dto.PedidoDto
import com.example.golazo_store.data.remote.dto.PedidoRequestDto
import com.example.golazo_store.data.remote.dto.PedidoAdminDto
import com.example.golazo_store.data.remote.dto.UpdateEstadoDto
import javax.inject.Inject

class PedidoRemoteDataSource @Inject constructor(
    private val api: GolazoApi
) {
    suspend fun createPedido(request: PedidoRequestDto): Result<PedidoDto> {
        return try {
            val response = api.createPedido(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Cuerpo de respuesta vacío"))
            } else {
                val errorBody = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("Error al crear pedido: $errorBody"))
            }
        } catch (e: Exception) {
            val isEndOfInput = e.message?.contains("End of input", ignoreCase = true) == true || 
                               e is java.io.EOFException

            if (isEndOfInput) {
                // Workaround: La API de C# crashea al intentar serializar el objeto Pedido completo (Error de Referencia Circular), 
                // pero sí inserta el pedido en la base de datos correctamente.
                // Como el pedido se creó, forzamos un Success temporal para no romper el flujo.
                Result.success(PedidoDto(id = 0, numeroPedido = "Creado (Revisa tus pedidos)", estado = "PROCESANDO", total = 0.0))
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun getAdminPedidos(): Result<List<PedidoAdminDto>> {
        return try {
            val response = api.getAdminPedidos()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Cuerpo de respuesta vacío"))
            } else {
                Result.failure(Exception("Error al obtener pedidos: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMisPedidos(usuarioId: Int): Result<List<PedidoAdminDto>> {
        return try {
            val response = api.getMisPedidos(usuarioId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Cuerpo de respuesta vacío"))
            } else {
                Result.failure(Exception("Error al obtener mis pedidos: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAdminPedidoById(id: Int): Result<PedidoAdminDto> {
        return try {
            val response = api.getAdminPedidoById(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Cuerpo de respuesta vacío"))
            } else {
                Result.failure(Exception("Error al obtener pedido: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePedidoEstado(pedidoId: Int, estado: String): Result<Unit> {
        return try {
            val response = api.updatePedidoEstado(pedidoId, UpdateEstadoDto(estado))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al actualizar pedido: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
