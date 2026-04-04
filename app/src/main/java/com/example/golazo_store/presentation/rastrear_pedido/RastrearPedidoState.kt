package com.example.golazo_store.presentation.rastrear_pedido

import com.example.golazo_store.domain.model.PedidoAdmin

data class RastrearPedidoState(
    val isLoading: Boolean = false,
    val pedido: PedidoAdmin? = null,
    val error: String? = null
)
