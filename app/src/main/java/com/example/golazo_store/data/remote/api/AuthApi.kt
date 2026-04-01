package com.example.golazo_store.data.remote.api

import com.example.golazo_store.data.remote.dto.UsuarioLoginDTO
import com.example.golazo_store.data.remote.dto.UsuarioRegistroDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

import com.example.golazo_store.data.remote.dto.LoginResponseDTO

interface AuthApi {
    @POST("api/Auth/login")
    suspend fun login(@Body request: UsuarioLoginDTO): Response<LoginResponseDTO>

    @POST("api/Auth/registro")
    suspend fun registro(@Body request: UsuarioRegistroDTO): Response<ResponseBody>
}
