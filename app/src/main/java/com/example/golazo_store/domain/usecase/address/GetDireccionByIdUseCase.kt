package com.example.golazo_store.domain.usecase.address

import com.example.golazo_store.domain.model.Direccion
import com.example.golazo_store.domain.repository.DireccionRepository
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDireccionByIdUseCase @Inject constructor(
    private val repository: DireccionRepository
) {
    operator fun invoke(id: Int): Flow<Resource<Direccion>> {
        return repository.getDireccionById(id)
    }
}
