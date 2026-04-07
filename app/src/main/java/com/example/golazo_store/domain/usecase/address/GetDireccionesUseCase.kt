package com.example.golazo_store.domain.usecase.address

import com.example.golazo_store.domain.model.Direccion
import com.example.golazo_store.domain.repository.DireccionRepository
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDireccionesUseCase @Inject constructor(
    private val repository: DireccionRepository
) {
    operator fun invoke(): Flow<Resource<List<Direccion>>> {
        return repository.getDirecciones()
    }
}
