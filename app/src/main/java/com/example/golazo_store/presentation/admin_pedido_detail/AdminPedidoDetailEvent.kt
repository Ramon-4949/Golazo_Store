package com.example.golazo_store.presentation.admin_pedido_detail

sealed class AdminPedidoDetailEvent {
    data class SelectEstado(val estado: String) : AdminPedidoDetailEvent()
    object SaveChanges : AdminPedidoDetailEvent()
    object RetryLoading : AdminPedidoDetailEvent()
}
