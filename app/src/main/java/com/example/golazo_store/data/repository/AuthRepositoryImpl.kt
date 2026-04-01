package com.example.golazo_store.data.repository

import com.example.golazo_store.data.local.SessionManager
import com.example.golazo_store.data.remote.dto.UsuarioLoginDTO
import com.example.golazo_store.data.remote.dto.UsuarioRegistroDTO
import com.example.golazo_store.data.remote.remotedatasource.AuthRemoteDataSource
import com.example.golazo_store.domain.repository.AuthRepository
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val sessionManager: SessionManager
) : AuthRepository {
    override fun login(correo: String, password: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val response = remoteDataSource.login(UsuarioLoginDTO(correo, password))
        response.fold(
            onSuccess = { loginResponse -> 
                loginResponse.usuario?.let { userDto ->
                    sessionManager.saveUserSession(userDto.toDomain())
                }
                emit(Resource.Success(loginResponse.mensaje ?: "Login Exitoso")) 
            },
            onFailure = { emit(Resource.Error(it.message ?: "Error al Iniciar Sesión")) }
        )
    }

    override fun register(nombre: String, correo: String, password: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val response = remoteDataSource.register(UsuarioRegistroDTO(nombre, correo, password))
        response.fold(
            onSuccess = { emit(Resource.Success(it)) },
            onFailure = { emit(Resource.Error(it.message ?: "Error al Registrarse")) }
        )
    }
}
