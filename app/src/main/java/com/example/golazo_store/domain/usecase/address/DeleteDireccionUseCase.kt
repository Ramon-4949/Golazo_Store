package com.example.golazo_store.domain.usecase.address

import com.example.golazo_store.domain.repository.DireccionRepository
import com.example.golazo_store.domain.utils.Resource
import javax.inject.Inject

class DeleteDireccionUseCase @Inject constructor(
    private val repository: DireccionRepository
) {
    suspend operator fun invoke(id: Int): Resource<Unit> {
        return repository.deleteDireccion(id)
    }
}
