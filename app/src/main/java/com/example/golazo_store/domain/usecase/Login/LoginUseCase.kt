package com.example.golazo_store.domain.usecase.Login

import com.example.golazo_store.domain.repository.AuthRepository
import com.example.golazo_store.domain.usecase.validation.ValidateEmailUseCase
import com.example.golazo_store.domain.usecase.validation.ValidatePasswordUseCase
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase
) {
    operator fun invoke(correo: String, password: String): Flow<LoginResult> = flow {
        val emailResult = validateEmail(correo)
        val passwordResult = validatePassword(password)

        if (!emailResult.successful || !passwordResult.successful) {
            emit(LoginResult.ValidationError(
                emailError = emailResult.errorMessage,
                passwordError = passwordResult.errorMessage
            ))
            return@flow
        }

        emit(LoginResult.Loading)

        repository.login(correo, password).collect { result ->
            when (result) {
                is Resource.Success -> emit(LoginResult.Success)
                is Resource.Error -> emit(LoginResult.Error(result.message ?: "Ocurrió un error al iniciar sesión"))
                else -> {}
            }
        }
    }
}

sealed class LoginResult {
    object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
    object Loading : LoginResult()
    data class ValidationError(
        val emailError: String? = null,
        val passwordError: String? = null
    ) : LoginResult()
}