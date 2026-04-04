package com.example.golazo_store.data.repository

import com.example.golazo_store.data.remote.remotedatasource.PedidoRemoteDataSource
import com.example.golazo_store.domain.model.Pedido
import com.example.golazo_store.domain.model.PedidoRegistro
import com.example.golazo_store.domain.repository.PedidoRepository
import com.example.golazo_store.domain.model.PedidoAdmin
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PedidoRepositoryImpl @Inject constructor(
    private val remoteDataSource: PedidoRemoteDataSource
) : PedidoRepository {

    override suspend fun createPedido(request: PedidoRegistro): Resource<Pedido> {
        val dto = request.toDto()
        val response = remoteDataSource.createPedido(dto)

        return response.fold(
            onSuccess = { pedidoDto ->
                val pedido = Pedido(
                    id = pedidoDto.id ?: 0,
                    numeroPedido = pedidoDto.numeroPedido ?: "",
                    estado = pedidoDto.estado ?: "",
                    fechaPedido = pedidoDto.fechaPedido ?: "",
                    total = pedidoDto.total ?: 0.0
                )
                Resource.Success(pedido)
            },
            onFailure = { error ->
                Resource.Error(error.message ?: "Error desconocido")
            }
        )
    }

    override fun getAdminPedidos(): Flow<Resource<List<PedidoAdmin>>> = flow {
        emit(Resource.Loading())
        val result = remoteDataSource.getAdminPedidos()
        result.fold(
            onSuccess = { dtos ->
                emit(Resource.Success(dtos.map { it.toDomain() }))
            },
            onFailure = {
                emit(Resource.Error(it.message ?: "An unexpected error occurred"))
            }
        )
    }

    override fun getMisPedidos(): Flow<Resource<List<PedidoAdmin>>> = flow {
        emit(Resource.Loading())
        val result = remoteDataSource.getMisPedidos()
        result.fold(
            onSuccess = { dtos ->
                emit(Resource.Success(dtos.map { it.toDomain() }))
            },
            onFailure = {
                emit(Resource.Error(it.message ?: "An unexpected error occurred"))
            }
        )
    }

    override fun getAdminPedidoById(id: Int): Flow<Resource<PedidoAdmin>> = flow {
        emit(Resource.Loading())
        val result = remoteDataSource.getAdminPedidoById(id)
        result.fold(
            onSuccess = { dto ->
                emit(Resource.Success(dto.toDomain()))
            },
            onFailure = {
                emit(Resource.Error(it.message ?: "An unexpected error occurred"))
            }
        )
    }

    override suspend fun updatePedidoEstado(pedidoId: Int, estado: String): Resource<Unit> {
        val result = remoteDataSource.updatePedidoEstado(pedidoId, estado)
        return result.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error(it.message ?: "Failed to update state") }
        )
    }
}
