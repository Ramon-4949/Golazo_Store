package com.example.golazo_store.domain.repository

import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(correo: String, password: String): Flow<Resource<String>>
    fun register(nombre: String, correo: String, password: String): Flow<Resource<String>>
}
