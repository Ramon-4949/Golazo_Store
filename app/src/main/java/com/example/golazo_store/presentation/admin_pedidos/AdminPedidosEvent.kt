package com.example.golazo_store.presentation.admin_pedidos

sealed class AdminPedidosEvent {
    data class SelectFilter(val filter: String) : AdminPedidosEvent()
    object LoadPedidos : AdminPedidosEvent()
}
