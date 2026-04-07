package com.example.golazo_store.domain.usecase.payment

import com.example.golazo_store.domain.model.MetodoPago
import com.example.golazo_store.domain.repository.MetodoPagoRepository
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMetodosPagoUseCase @Inject constructor(
    private val repository: MetodoPagoRepository
) {
    operator fun invoke(): Flow<Resource<List<MetodoPago>>> {
        return repository.getMetodosPago()
    }
}
