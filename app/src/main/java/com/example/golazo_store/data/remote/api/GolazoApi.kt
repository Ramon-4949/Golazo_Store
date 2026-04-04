package com.example.golazo_store.data.remote.api

import com.example.golazo_store.data.remote.dto.CamisetaDto
import com.example.golazo_store.data.remote.dto.CategoriaDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Multipart
import retrofit2.http.Part
import okhttp3.MultipartBody
import com.example.golazo_store.data.remote.dto.UploadDto
import com.example.golazo_store.data.remote.dto.PedidoAdminDto
import com.example.golazo_store.data.remote.dto.UpdateEstadoDto

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
    
    @GET("api/Categorias")
    suspend fun getCategorias(): Response<List<CategoriaDto>>
    
    @Multipart
    @POST("api/Upload")
    suspend fun uploadImage(@Part imagen: MultipartBody.Part): Response<UploadDto>

    @GET("api/Direcciones")
    suspend fun getDirecciones(): Response<List<com.example.golazo_store.data.remote.dto.DireccionDto>>

    @GET("api/Direcciones/{id}")
    suspend fun getDireccionById(@Path("id") id: Int): Response<com.example.golazo_store.data.remote.dto.DireccionDto>

    @POST("api/Direcciones")
    suspend fun createDireccion(@Body direccion: com.example.golazo_store.data.remote.dto.DireccionDto): Response<com.example.golazo_store.data.remote.dto.DireccionDto>

    @PUT("api/Direcciones/{id}")
    suspend fun updateDireccion(@Path("id") id: Int, @Body direccion: com.example.golazo_store.data.remote.dto.DireccionDto): Response<Unit>

    @DELETE("api/Direcciones/{id}")
    suspend fun deleteDireccion(@Path("id") id: Int): Response<Unit>

    // --- MetodosPago ---
    @GET("api/MetodosPago")
    suspend fun getMetodosPago(): Response<List<com.example.golazo_store.data.remote.dto.MetodoPagoDto>>

    @POST("api/MetodosPago")
    suspend fun createMetodoPago(@Body request: com.example.golazo_store.data.remote.dto.MetodoPagoRequestDto): Response<com.example.golazo_store.data.remote.dto.MetodoPagoDto>

    @DELETE("api/MetodosPago/{id}")
    suspend fun deleteMetodoPago(@Path("id") id: Int): Response<Unit>

    @POST("api/Pedidos")
    suspend fun createPedido(@Body request: com.example.golazo_store.data.remote.dto.PedidoRequestDto): Response<com.example.golazo_store.data.remote.dto.PedidoDto>

    @GET("api/Pedidos")
    suspend fun getAdminPedidos(): Response<List<PedidoAdminDto>>

    @GET("api/Pedidos/mis-pedidos")
    suspend fun getMisPedidos(): Response<List<PedidoAdminDto>>

    @GET("api/Pedidos/{id}")
    suspend fun getAdminPedidoById(@Path("id") id: Int): Response<PedidoAdminDto>

    @PUT("api/Pedidos/{id}/estado")
    suspend fun updatePedidoEstado(@Path("id") id: Int, @Body request: UpdateEstadoDto): Response<Unit>
}