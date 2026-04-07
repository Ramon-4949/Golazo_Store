package com.example.golazo_store.domain.usecase.payment

import com.example.golazo_store.domain.repository.MetodoPagoRepository
import com.example.golazo_store.domain.utils.Resource
import javax.inject.Inject

class DeleteMetodoPagoUseCase @Inject constructor(
    private val repository: MetodoPagoRepository
) {
    suspend operator fun invoke(id: Int): Resource<Unit> {
        return repository.deleteMetodoPago(id)
    }
}
