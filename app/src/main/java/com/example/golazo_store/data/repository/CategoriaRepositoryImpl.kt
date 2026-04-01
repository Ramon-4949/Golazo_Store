package com.example.golazo_store.data.repository

import com.example.golazo_store.data.remote.api.GolazoApi
import com.example.golazo_store.domain.model.Categoria
import com.example.golazo_store.domain.repository.CategoriaRepository
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CategoriaRepositoryImpl @Inject constructor(
    private val api: GolazoApi
) : CategoriaRepository {

    override fun getCategorias(): Flow<Resource<List<Categoria>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getCategorias()
            if (response.isSuccessful) {
                val categorias = response.body()?.map { it.toDomain() } ?: emptyList()
                emit(Resource.Success(categorias))
            } else {
                emit(Resource.Error("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Excepción: ${e.message}"))
        }
    }
}
