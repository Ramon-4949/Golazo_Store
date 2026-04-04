package com.example.golazo_store.domain.repository

import com.example.golazo_store.domain.model.Pedido
import com.example.golazo_store.domain.model.PedidoAdmin
import com.example.golazo_store.domain.model.PedidoRegistro
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PedidoRepository {
    suspend fun createPedido(request: PedidoRegistro): Resource<Pedido>
    fun getAdminPedidos(): Flow<Resource<List<PedidoAdmin>>>
    fun getMisPedidos(): Flow<Resource<List<PedidoAdmin>>>
    fun getAdminPedidoById(id: Int): Flow<Resource<PedidoAdmin>>
    suspend fun updatePedidoEstado(pedidoId: Int, estado: String): Resource<Unit>
}
