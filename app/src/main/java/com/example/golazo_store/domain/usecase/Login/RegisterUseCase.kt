package com.example.golazo_store.domain.usecase.Login

import com.example.golazo_store.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(nombre: String, correo: String, password: String) = repository.register(nombre, correo, password)
}