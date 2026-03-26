package com.example.golazo_store.domain.usecase

import com.example.golazo_store.domain.model.Camiseta
import com.example.golazo_store.domain.repository.CamisetaRepository
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCamisetasUseCase @Inject constructor(
    private val repository: CamisetaRepository
) {
    operator fun invoke(): Flow<Resource<List<Camiseta>>> {
        return repository.getCamisetas()
    }
}