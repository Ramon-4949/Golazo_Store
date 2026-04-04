package com.example.golazo_store.presentation.mis_pedidos

import com.example.golazo_store.domain.model.PedidoAdmin

data class MisPedidosState(
    val isLoading: Boolean = false,
    val pedidos: List<PedidoAdmin> = emptyList(),
    val error: String? = null,
    val currentTab: Int = 0 // 0: Todos, 1: En curso, 2: Finalizados
)
