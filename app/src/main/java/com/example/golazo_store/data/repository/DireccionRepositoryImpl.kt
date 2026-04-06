package com.example.golazo_store.data.repository

import com.example.golazo_store.data.remote.remotedatasource.DireccionRemoteDataSource
import com.example.golazo_store.domain.model.Direccion
import com.example.golazo_store.domain.repository.DireccionRepository
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

import com.example.golazo_store.data.local.SessionManager

class DireccionRepositoryImpl @Inject constructor(
    private val remoteDataSource: DireccionRemoteDataSource,
    private val sessionManager: SessionManager
) : DireccionRepository {

    override fun getDirecciones(): Flow<Resource<List<Direccion>>> = flow {
        emit(Resource.Loading())
        val uId = sessionManager.getUserSession()?.id ?: 0
        val response = remoteDataSource.getDirecciones(uId)
        
        response.fold(
            onSuccess = { dtos ->
                val domainList = dtos.map { it.toDomain() }
                emit(Resource.Success(domainList))
            },
            onFailure = {
                emit(Resource.Error(it.message ?: "Error al obtener direcciones"))
            }
        )
    }

    override fun getDireccionById(id: Int): Flow<Resource<Direccion>> = flow {
        emit(Resource.Loading())
        val uId = sessionManager.getUserSession()?.id ?: 0
        val response = remoteDataSource.getDireccionById(id, uId)

        response.fold(
            onSuccess = { dto ->
                emit(Resource.Success(dto.toDomain()))
            },
            onFailure = {
                emit(Resource.Error(it.message ?: "Error al obtener la dirección"))
            }
        )
    }

    override suspend fun createDireccion(direccion: Direccion): Resource<Unit> {
        val uId = sessionManager.getUserSession()?.id ?: 0
        val dto = direccion.toDto(usuarioIdOverride = uId)
        val response = remoteDataSource.createDireccion(dto)

        return response.fold(
            onSuccess = {
                Resource.Success(Unit)
            },
            onFailure = {
                Resource.Error(it.message ?: "Error al crear la dirección")
            }
        )
    }

    override suspend fun updateDireccion(id: Int, direccion: Direccion): Resource<Unit> {
        val uId = sessionManager.getUserSession()?.id ?: 0
        val dto = direccion.toDto(usuarioIdOverride = uId)
        val response = remoteDataSource.updateDireccion(id, dto)

        return response.fold(
            onSuccess = {
                Resource.Success(Unit)
            },
            onFailure = {
                Resource.Error(it.message ?: "Error al actualizar la dirección")
            }
        )
    }

    override suspend fun deleteDireccion(id: Int): Resource<Unit> {
        val uId = sessionManager.getUserSession()?.id ?: 0
        val response = remoteDataSource.deleteDireccion(id, uId)

        return response.fold(
            onSuccess = {
                Resource.Success(Unit)
            },
            onFailure = {
                Resource.Error(it.message ?: "Error al eliminar la dirección")
            }
        )
    }
}
