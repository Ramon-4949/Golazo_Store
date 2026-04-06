package com.example.golazo_store.data.remote.api

import com.example.golazo_store.data.remote.dto.UsuarioLoginDTO
import com.example.golazo_store.data.remote.dto.UsuarioRegistroDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path

import com.example.golazo_store.data.remote.dto.LoginResponseDTO

interface AuthApi {
    @POST("api/Auth/login")
    suspend fun login(@Body request: UsuarioLoginDTO): Response<LoginResponseDTO>

    @POST("api/Auth/registro")
    suspend fun registro(@Body request: UsuarioRegistroDTO): Response<ResponseBody>

    @PUT("api/Auth/perfil/{id}")
    suspend fun updateProfile(@Path("id") id: Int, @Body request: com.example.golazo_store.data.remote.dto.UpdateProfileRequest): Response<ResponseBody>

    @DELETE("api/Auth/cuenta/{id}")
    suspend fun deleteAccount(@Path("id") id: Int): Response<ResponseBody>
}
