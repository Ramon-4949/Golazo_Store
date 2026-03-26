package com.example.golazo_store.data.remote.api

import com.example.golazo_store.data.remote.dto.CamisetaDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GolazoApi {
    @GET("api/Camisetas")
    suspend fun getCamisetas(): Response<List<CamisetaDto>>
    @GET("api/Camisetas/{id}")
    suspend fun getCamisetaById(@Path("id") id: Int): Response<CamisetaDto>
    @POST("api/Camisetas")
    suspend fun createCamiseta(@Body camiseta: CamisetaDto): Response<CamisetaDto>
    @PUT("api/Camisetas/{id}")
    suspend fun updateCamiseta(@Path("id") id: Int, @Body camiseta: CamisetaDto): Response<Unit>
    @DELETE("api/Camisetas/{id}")
    suspend fun deleteCamiseta(@Path("id") id: Int): Response<Unit>
}