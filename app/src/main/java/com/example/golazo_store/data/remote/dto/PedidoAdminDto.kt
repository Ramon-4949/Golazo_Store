package com.example.golazo_store.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PedidoAdminDto(
    val id: Int? = null,
    val usuarioId: Int? = null,
    val numeroPedido: String? = null,
    val fechaPedido: String? = null,
    val estado: String? = null,
    val total: Double? = null,
    @Json(name = "usuario") val usuario: UsuarioPedidoDto? = null,
    @Json(name = "user") val user: UsuarioPedidoDto? = null,
    @Json(name = "items") val items: List<ItemPedidoAdminDto>? = emptyList(),
    @Json(name = "direccion") val direccion: DireccionDto? = null,
    @Json(name = "direccionEnvio") val direccionEnvio: String? = null
) {
    fun toDomain(): com.example.golazo_store.domain.model.PedidoAdmin {
        return com.example.golazo_store.domain.model.PedidoAdmin(
            id = id ?: 0,
            usuarioId = usuarioId ?: 0,
            numeroPedido = numeroPedido.orEmpty(),
            fechaPedido = fechaPedido.orEmpty(),
            estado = estado.orEmpty(),
            total = total ?: 0.0,
            usuario = (usuario ?: user)?.toDomain(),
            items = items?.map { it.toDomain() } ?: emptyList(),
            direccion = direccion?.toDomain(),
            direccionEnvio = direccionEnvio
        )
    }
}

@JsonClass(generateAdapter = true)
data class UsuarioPedidoDto(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "nombre") val nombre: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "correo") val correo: String? = null,
    @Json(name = "email") val email: String? = null
) {
    fun toDomain(): com.example.golazo_store.domain.model.UsuarioPedido {
        return com.example.golazo_store.domain.model.UsuarioPedido(
            id = id ?: 0,
            nombre = nombre ?: name.orEmpty(),
            correo = correo ?: email.orEmpty()
        )
    }
}

@JsonClass(generateAdapter = true)
data class ItemPedidoAdminDto(
    val id: Int? = null,
    val pedidoId: Int? = null,
    val camisetaId: Int? = null,
    val cantidad: Int? = null,
    val precioUnitario: Double? = null,
    val camiseta: CamisetaPedidoDto? = null
) {
    fun toDomain(): com.example.golazo_store.domain.model.ItemPedidoAdmin {
        return com.example.golazo_store.domain.model.ItemPedidoAdmin(
            id = id ?: 0,
            camisetaId = camisetaId ?: 0,
            cantidad = cantidad ?: 0,
            precioUnitario = precioUnitario ?: 0.0,
            camiseta = camiseta?.toDomain()
        )
    }
}

@JsonClass(generateAdapter = true)
data class CamisetaPedidoDto(
    val id: Int? = null,
    val nombre: String? = null,
    val imagenUrl: String? = null
) {
    fun toDomain(): com.example.golazo_store.domain.model.CamisetaPedido {
        return com.example.golazo_store.domain.model.CamisetaPedido(
            id = id ?: 0,
            nombre = nombre.orEmpty(),
            imagenUrl = imagenUrl.orEmpty()
        )
    }
}
