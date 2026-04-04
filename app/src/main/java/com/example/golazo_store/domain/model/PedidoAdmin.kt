package com.example.golazo_store.domain.model

data class PedidoAdmin(
    val id: Int,
    val usuarioId: Int,
    val numeroPedido: String,
    val fechaPedido: String,
    val estado: String,
    val total: Double,
    val usuario: UsuarioPedido?,
    val items: List<ItemPedidoAdmin>,
    val direccion: Direccion?,
    val direccionEnvio: String?
)

data class UsuarioPedido(
    val id: Int,
    val nombre: String,
    val correo: String
)

data class ItemPedidoAdmin(
    val id: Int,
    val camisetaId: Int,
    val cantidad: Int,
    val precioUnitario: Double,
    val camiseta: CamisetaPedido?
)

data class CamisetaPedido(
    val id: Int,
    val nombre: String,
    val imagenUrl: String
)
