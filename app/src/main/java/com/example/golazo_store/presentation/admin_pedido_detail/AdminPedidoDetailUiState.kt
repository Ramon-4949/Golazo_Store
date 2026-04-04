package com.example.golazo_store.presentation.admin_pedido_detail

import com.example.golazo_store.domain.model.PedidoAdmin

data class AdminPedidoDetailUiState(
    val isLoading: Boolean = false,
    val pedido: PedidoAdmin? = null,
    val error: String? = null,
    val selectedEstado: String = "",
    val availableEstados: List<String> = listOf("Pendiente", "Enviado", "Completado", "Cancelado"),
    val isUpdating: Boolean = false,
    val updateSuccess: Boolean = false
)
