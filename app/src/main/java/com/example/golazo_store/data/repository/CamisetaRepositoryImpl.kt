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
import javax.inject.Inject

class CamisetaRepositoryImpl @Inject constructor(
    private val remoteDataSource: CamisetaRemoteDataSource,
    private val localDataSource: CamisetaDao
) : CamisetaRepository {

    override fun getCamisetas(): Flow<Resource<List<Camiseta>>> = flow {
        emit(Resource.Loading())

        try {
            // 1. Buscamos datos frescos en la API
            val response = remoteDataSource.getCamisetas()
            response.onSuccess { camisetasDto ->
                val camisetasDomain = camisetasDto.map { it.toDomain() }
                // 2. Guardamos todo en Room
                camisetasDomain.forEach { localDataSource.upsert(it.toEntity()) }
            }
        } catch (e: Exception) {
            // Si no hay internet, ignoramos el error, Room nos salvará abajo
        }

        // 3. Nos quedamos escuchando a Room. Si Room cambia, la UI se actualiza sola.
        emitAll(
            localDataSource.observeAll().map { entities ->
                Resource.Success(entities.map { it.toDomain() })
            }
        )
    }

    override fun getCamisetaById(id: Int): Flow<Resource<Camiseta>> = flow {
        emit(Resource.Loading())

        try {
            // 1. Petición a la API para el stock en tiempo real
            val response = remoteDataSource.getCamisetaById(id)
            response.onSuccess { remoteDto ->
                // 2. Actualizamos el stock fresco en Room
                localDataSource.upsert(remoteDto.toDomain().toEntity())
            }
        } catch (e: Exception) {
            // Ignoramos error de red aquí
        }

        // 3. Emitimos desde Room. Esto asegura que la talla y el stock cambien mágicamente.
        emitAll(
            localDataSource.observeById(id).map { entity ->
                if (entity != null) {
                    Resource.Success(entity.toDomain())
                } else {
                    Resource.Error("Camiseta no encontrada en la base de datos local")
                }
            }
        )
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
                onFailure = { Resource.Error(it.message ?: "Error al crear la camiseta") }
            )
        } else {
            val response = remoteDataSource.updateCamiseta(camiseta.id, dto)
            return response.fold(
                onSuccess = {
                    localDataSource.upsert(camiseta.toEntity())
                    Resource.Success(Unit)
                },
                onFailure = { Resource.Error(it.message ?: "Error al actualizar la camiseta") }
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
            onFailure = { Resource.Error(it.message ?: "Error al eliminar") }
        )
    }

    override suspend fun syncCamisetas(): Resource<Unit> {
        return try {
            val response = remoteDataSource.getCamisetas()
            response.fold(
                onSuccess = { camisetasDto ->
                    val camisetasDomain = camisetasDto.map { it.toDomain() }
                    camisetasDomain.forEach { localDataSource.upsert(it.toEntity()) }
                    Resource.Success(Unit)
                },
                onFailure = { Resource.Error(it.message ?: "Error al sincronizar") }
            )
        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.message}")
        }
    }
}