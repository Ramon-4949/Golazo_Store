package com.example.golazo_store.domain.repository

import com.example.golazo_store.domain.model.Categoria
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CategoriaRepository {
    fun getCategorias(): Flow<Resource<List<Categoria>>>
}
