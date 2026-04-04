package com.example.golazo_store.domain.repository

import com.example.golazo_store.domain.model.MetodoPago
import com.example.golazo_store.domain.model.MetodoPagoRegistro
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MetodoPagoRepository {
    fun getMetodosPago(): Flow<Resource<List<MetodoPago>>>
    suspend fun createMetodoPago(request: MetodoPagoRegistro): Resource<Unit>
    suspend fun deleteMetodoPago(id: Int): Resource<Unit>
}
