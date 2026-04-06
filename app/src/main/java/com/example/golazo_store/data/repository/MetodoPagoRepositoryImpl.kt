package com.example.golazo_store.data.repository

import com.example.golazo_store.data.remote.remotedatasource.MetodoPagoRemoteDataSource
import com.example.golazo_store.domain.model.MetodoPago
import com.example.golazo_store.domain.model.MetodoPagoRegistro
import com.example.golazo_store.domain.repository.MetodoPagoRepository
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

import com.example.golazo_store.data.local.SessionManager

class MetodoPagoRepositoryImpl @Inject constructor(
    private val remoteDataSource: MetodoPagoRemoteDataSource,
    private val sessionManager: SessionManager
) : MetodoPagoRepository {

    override fun getMetodosPago(): Flow<Resource<List<MetodoPago>>> = flow {
        emit(Resource.Loading())
        val uId = sessionManager.getUserSession()?.id ?: 0
        val response = remoteDataSource.getMetodosPago(uId)

        response.fold(
            onSuccess = { dtos ->
                val domainList = dtos.map { it.toDomain() }
                emit(Resource.Success(domainList))
            },
            onFailure = {
                emit(Resource.Error(it.message ?: "Error al obtener métodos de pago"))
            }
        )
    }

    override suspend fun createMetodoPago(request: MetodoPagoRegistro): Resource<Unit> {
        val uId = sessionManager.getUserSession()?.id ?: 0
        val dto = request.toDto(uId)
        val response = remoteDataSource.createMetodoPago(dto)

        return response.fold(
            onSuccess = {
                Resource.Success(Unit)
            },
            onFailure = {
                Resource.Error(it.message ?: "Error al guardar el método de pago")
            }
        )
    }

    override suspend fun deleteMetodoPago(id: Int): Resource<Unit> {
        val uId = sessionManager.getUserSession()?.id ?: 0
        val response = remoteDataSource.deleteMetodoPago(id, uId)

        return response.fold(
            onSuccess = {
                Resource.Success(Unit)
            },
            onFailure = {
                Resource.Error(it.message ?: "Error al eliminar el método de pago")
            }
        )
    }
}
