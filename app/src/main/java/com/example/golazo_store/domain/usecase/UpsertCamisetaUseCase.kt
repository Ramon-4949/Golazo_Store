package com.example.golazo_store.domain.usecase

import com.example.golazo_store.domain.model.Camiseta
import com.example.golazo_store.domain.repository.CamisetaRepository
import com.example.golazo_store.domain.utils.Resource
import javax.inject.Inject

class UpsertCamisetaUseCase @Inject constructor(
    private val repository: CamisetaRepository
) {
    suspend operator fun invoke(camiseta: Camiseta): Resource<Unit> {
        if (camiseta.precio <= 0) {
            return Resource.Error("El precio debe ser mayor a 0")
        }
        if (camiseta.equipo.isBlank()) {
            return Resource.Error("El nombre del equipo no puede estar vacío")
        }

        return repository.upsertCamiseta(camiseta)
    }
}