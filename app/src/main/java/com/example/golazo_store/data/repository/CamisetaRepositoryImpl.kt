package com.example.golazo_store.data.repository

import com.example.golazo_store.data.local.dao.CamisetaDao
import com.example.golazo_store.data.local.entity.toDomain
import com.example.golazo_store.data.local.entity.toEntity
import com.example.golazo_store.data.remote.remotedatasource.CamisetaRemoteDataSource
import com.example.golazo_store.domain.model.Camiseta
import com.example.golazo_store.domain.repository.CamisetaRepository
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CamisetaRepositoryImpl @Inject constructor(
    private val remoteDataSource: CamisetaRemoteDataSource,
    private val localDataSource: CamisetaDao
) : CamisetaRepository {

    override fun getCamisetas(): Flow<Resource<List<Camiseta>>> = flow {
        emit(Resource.Loading())

        val response = remoteDataSource.getCamisetas()
        response.onSuccess { camisetasDto ->
            val camisetasDomain = camisetasDto.map { it.toDomain() }
            camisetasDomain.forEach { localDataSource.upsert(it.toEntity()) }
        }.onFailure {
            emit(Resource.Error(it.message ?: "Error de sincronización con el servidor"))
        }

        localDataSource.observeAll().collect { entities ->
            emit(Resource.Success(entities.map { it.toDomain() }))
        }
    }

    override fun getCamisetaById(id: Int): Flow<Resource<Camiseta>> = flow {
        emit(Resource.Loading())

        val response = remoteDataSource.getCamisetaById(id)
        response.onSuccess { camisetaDto ->
            localDataSource.upsert(camisetaDto.toDomain().toEntity())
        }.onFailure {
            emit(Resource.Error(it.message ?: "Error de red"))
        }

        val localCamiseta = localDataSource.getById(id)
        if (localCamiseta != null) {
            emit(Resource.Success(localCamiseta.toDomain()))
        } else {
            emit(Resource.Error("Camiseta no encontrada en la base de datos local"))
        }
    }
}