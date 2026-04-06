package com.example.golazo_store.data.remote.remotedatasource

import com.example.golazo_store.data.remote.api.AuthApi
import com.example.golazo_store.data.remote.dto.UsuarioLoginDTO
import com.example.golazo_store.data.remote.dto.UsuarioRegistroDTO
import com.example.golazo_store.data.remote.dto.LoginResponseDTO
import retrofit2.HttpException
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val api: AuthApi
) {
    suspend fun login(request: UsuarioLoginDTO): Result<LoginResponseDTO> {
        return try {
            val response = api.login(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Respuesta vacía o nula del servidor"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("Error de red: ${response.code()} - $errorMsg"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Error de servidor", e))
        } catch (e: Exception) {
            Result.failure(Exception("Error desconocido: ${e.message}", e))
        }
    }

    suspend fun register(request: UsuarioRegistroDTO): Result<String> {
        return try {
            val response = api.registro(request)
            if (response.isSuccessful) {
                Result.success(response.body()?.string() ?: "Registro Exitoso")
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("Error de red: ${response.code()} - $errorMsg"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Error de servidor", e))
        } catch (e: Exception) {
            Result.failure(Exception("Error desconocido: ${e.message}", e))
        }
    }

    suspend fun updateProfile(id: Int, request: com.example.golazo_store.data.remote.dto.UpdateProfileRequest): Result<String> {
        return try {
            val response = api.updateProfile(id, request)
            if (response.isSuccessful) {
                Result.success(response.body()?.string() ?: "Perfil actualizado")
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("Error de red: ${response.code()} - $errorMsg"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Error de servidor", e))
        } catch (e: Exception) {
            Result.failure(Exception("Error desconocido: ${e.message}", e))
        }
    }

    suspend fun deleteAccount(id: Int): Result<String> {
        return try {
            val response = api.deleteAccount(id)
            if (response.isSuccessful) {
                Result.success(response.body()?.string() ?: "Cuenta eliminada")
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception("Error de red: ${response.code()} - $errorMsg"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Error de servidor", e))
        } catch (e: Exception) {
            Result.failure(Exception("Error desconocido: ${e.message}", e))
        }
    }
}
