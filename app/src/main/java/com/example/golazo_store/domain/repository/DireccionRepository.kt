package com.example.golazo_store.domain.repository

import com.example.golazo_store.domain.model.Direccion
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface DireccionRepository {
    fun getDirecciones(): Flow<Resource<List<Direccion>>>
    fun getDireccionById(id: Int): Flow<Resource<Direccion>>
    suspend fun createDireccion(direccion: Direccion): Resource<Unit>
    suspend fun updateDireccion(id: Int, direccion: Direccion): Resource<Unit>
    suspend fun deleteDireccion(id: Int): Resource<Unit>
}
