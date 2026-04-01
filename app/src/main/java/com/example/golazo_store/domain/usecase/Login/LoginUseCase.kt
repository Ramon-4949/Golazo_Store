package com.example.golazo_store.domain.usecase.Login

import com.example.golazo_store.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(correo: String, password: String) = repository.login(correo, password)
}