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

import com.example.golazo_store.domain.repository.FavoritesRepository

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val sessionManager: SessionManager,
    private val favoritesRepository: FavoritesRepository
) : AuthRepository {
    override fun login(correo: String, password: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val response = remoteDataSource.login(UsuarioLoginDTO(correo, password))
        response.fold(
            onSuccess = { loginResponse -> 
                loginResponse.usuario?.let { userDto ->
                    sessionManager.saveUserSession(userDto.toDomain())
                    favoritesRepository.syncDownFavorites()
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

    override fun updateProfile(id: Int, nombreUsuario: String, nuevaContrasena: String?): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val response = remoteDataSource.updateProfile(id, com.example.golazo_store.data.remote.dto.UpdateProfileRequest(nombreUsuario, nuevaContrasena))
        response.fold(
            onSuccess = {
                // Update local session to display the new name immediately
                val currentSession = sessionManager.getUserSession()
                if (currentSession != null && currentSession.id == id) {
                    sessionManager.saveUserSession(currentSession.copy(nombre = nombreUsuario))
                }
                emit(Resource.Success(it))
            },
            onFailure = { emit(Resource.Error(it.message ?: "Error al actualizar perfil")) }
        )
    }

    override fun deleteAccount(id: Int): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val response = remoteDataSource.deleteAccount(id)
        response.fold(
            onSuccess = {
                favoritesRepository.clearAllFavorites()
                sessionManager.clearSession()
                emit(Resource.Success(it))
            },
            onFailure = { emit(Resource.Error(it.message ?: "Error al eliminar cuenta")) }
        )
    }
}
