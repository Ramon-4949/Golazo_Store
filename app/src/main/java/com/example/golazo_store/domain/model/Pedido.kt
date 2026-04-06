package com.example.golazo_store.domain.model

data class Pedido(
    val id: Int,
    val numeroPedido: String,
    val estado: String,
    val fechaPedido: String,
    val total: Double
)

data class PedidoRegistro(
    val direccionId: Int,
    val metodoPagoId: Int,
    val items: List<DetallePedido>
) {
    fun toDto(usuarioId: Int) = com.example.golazo_store.data.remote.dto.PedidoRequestDto(
        usuarioId = usuarioId,
        direccionId = direccionId,
        metodoPagoId = metodoPagoId,
        items = items.map { it.toDto() }
    )
}

data class DetallePedido(
    val camisetaId: Int,
    val cantidad: Int
) {
    fun toDto() = com.example.golazo_store.data.remote.dto.DetallePedidoDto(
        camisetaId = camisetaId,
        cantidad = cantidad
    )
}
