package com.example.golazo_store.data.remote.api

import com.example.golazo_store.data.remote.dto.CamisetaDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GolazoApi {
    @GET("api/Camisetas")
    suspend fun getCamisetas(): Response<List<CamisetaDto>>

    @GET("api/Camisetas/{id}")
    suspend fun getCamisetaById(@Path("id") id: Int): Response<CamisetaDto>
}