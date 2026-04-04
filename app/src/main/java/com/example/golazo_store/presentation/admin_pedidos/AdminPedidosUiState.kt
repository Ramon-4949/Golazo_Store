package com.example.golazo_store.presentation.admin_pedidos

import com.example.golazo_store.domain.model.PedidoAdmin

data class AdminPedidosUiState(
    val isLoading: Boolean = false,
    val pedidos: List<PedidoAdmin> = emptyList(),
    val filteredPedidos: List<PedidoAdmin> = emptyList(),
    val error: String? = null,
    val selectedFilter: String = "Todos",
    val filters: List<String> = listOf("Todos", "Pendiente", "Enviado", "Completado")
)
