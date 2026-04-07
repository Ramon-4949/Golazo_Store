package com.example.golazo_store.domain.usecase.profile

import com.example.golazo_store.domain.repository.AuthRepository
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(id: Int): Flow<Resource<String>> {
        return repository.deleteAccount(id)
    }
}
