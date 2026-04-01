package com.example.golazo_store.domain.usecase.camiseta

import com.example.golazo_store.domain.repository.CamisetaRepository
import com.example.golazo_store.domain.utils.Resource
import javax.inject.Inject

class DeleteCamisetaUseCase @Inject constructor(
    private val repository: CamisetaRepository
) {
    suspend operator fun invoke(id: Int): Resource<Unit> {
        return repository.deleteCamiseta(id)
    }
}