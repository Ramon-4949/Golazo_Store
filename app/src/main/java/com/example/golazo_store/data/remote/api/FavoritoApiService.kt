package com.example.golazo_store.data.remote.api

import com.example.golazo_store.data.remote.dto.FavoritoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoritoApiService {
    @POST("api/Favoritos")
    suspend fun addFavorite(@Body favoritoDto: FavoritoDto): Response<Unit>

    @DELETE("api/Favoritos/usuario/{usuarioId}/camiseta/{camisetaId}")
    suspend fun deleteFavorite(
        @Path("usuarioId") usuarioId: Int,
        @Path("camisetaId") camisetaId: Int
    ): Response<Unit>

    @GET("api/Favoritos/mis-favoritos/{usuarioId}")
    suspend fun getMisFavoritos(
        @Path("usuarioId") usuarioId: Int
    ): Response<List<FavoritoDto>>
}
