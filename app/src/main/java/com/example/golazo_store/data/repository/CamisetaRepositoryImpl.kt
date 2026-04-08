package com.example.golazo_store.data.repository

import com.example.golazo_store.data.local.dao.CamisetaDao
import com.example.golazo_store.data.local.entity.toDomain
import com.example.golazo_store.data.local.entity.toEntity
import com.example.golazo_store.data.remote.dto.CamisetaDto
import com.example.golazo_store.data.remote.remotedatasource.CamisetaRemoteDataSource
import com.example.golazo_store.domain.model.Camiseta
import com.example.golazo_store.domain.repository.CamisetaRepository
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
        }

        localDataSource.observeAll().collect { entities ->
            emit(Resource.Success(entities.map { it.toDomain() }))
        }
    }

    override fun getCamisetaById(id: Int): Flow<Resource<Camiseta>> = flow {
        emit(Resource.Loading())
        

        val localEntity = localDataSource.getById(id)
        val localCamiseta = localEntity?.toDomain()
        if (localCamiseta != null) {
            emit(Resource.Success(localCamiseta))
        }
        

        try {
            val response = remoteDataSource.getCamisetaById(id)
            response.onSuccess { remoteDto ->
                val remoteCamiseta = remoteDto.toDomain()
                

                localDataSource.upsert(remoteCamiseta.toEntity())
                

                emit(Resource.Success(remoteCamiseta))
            }.onFailure {
                if (localCamiseta == null) emit(Resource.Error("Error de servidor"))
            }
        } catch (e: Exception) {
            if (localCamiseta == null) emit(Resource.Error("Revisa tu conexión a internet"))
        }
    }

    override suspend fun upsertCamiseta(camiseta: Camiseta): Resource<Unit> {
        val dto = CamisetaDto(
            id = camiseta.id,
            nombre = camiseta.nombre,
            descripcion = camiseta.descripcion,
            precio = camiseta.precio,
            imagenUrl = camiseta.imagenUrl,
            stockS = camiseta.stockS,
            stockM = camiseta.stockM,
            stockL = camiseta.stockL,
            stockXL = camiseta.stockXL,
            stock2XL = camiseta.stock2XL,
            stockTotal = camiseta.stockTotal,
            categoriaId = camiseta.categoriaId
        )

        if (camiseta.id == 0) {
            val response = remoteDataSource.createCamiseta(dto)
            return response.fold(
                onSuccess = { camisetaCreada ->
                    localDataSource.upsert(camisetaCreada.toDomain().toEntity())
                    Resource.Success(Unit)
                },
                onFailure = { Resource.Error(it.message ?: "Error al crear la camiseta en el servidor") }
            )
        } else {
            val response = remoteDataSource.updateCamiseta(camiseta.id, dto)
            return response.fold(
                onSuccess = {
                    localDataSource.upsert(camiseta.toEntity())
                    Resource.Success(Unit)
                },
                onFailure = { Resource.Error(it.message ?: "Error al actualizar la camiseta en el servidor") }
            )
        }
    }

    override suspend fun deleteCamiseta(id: Int): Resource<Unit> {
        val response = remoteDataSource.deleteCamiseta(id)

        return response.fold(
            onSuccess = {
                val localEntity = localDataSource.getById(id)
                if (localEntity != null) {
                    localDataSource.delete(localEntity)
                }
                Resource.Success(Unit)
            },
            onFailure = { Resource.Error(it.message ?: "Error al eliminar la camiseta en el servidor") }
        )
    }
}