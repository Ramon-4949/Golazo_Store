package com.example.golazo_store.domain.usecase.Login

import com.example.golazo_store.domain.repository.AuthRepository
import com.example.golazo_store.domain.usecase.validation.ValidateEmailUseCase
import com.example.golazo_store.domain.usecase.validation.ValidateEmptyFieldUseCase
import com.example.golazo_store.domain.usecase.validation.ValidatePasswordUseCase
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    private val validateEmptyField: ValidateEmptyFieldUseCase
) {
    operator fun invoke(nombre: String, correo: String, password: String): Flow<RegisterResult> = flow {
        val nombreResult = validateEmptyField(nombre, "nombre")
        val emailResult = validateEmail(correo)
        val passwordResult = validatePassword(password)

        if (!nombreResult.successful || !emailResult.successful || !passwordResult.successful) {
            emit(RegisterResult.ValidationError(
                nombreError = nombreResult.errorMessage,
                emailError = emailResult.errorMessage,
                passwordError = passwordResult.errorMessage
            ))
            return@flow
        }

        emit(RegisterResult.Loading)

        repository.register(nombre, correo, password)
            .catch { e ->
                emit(RegisterResult.Error(e.message ?: "Ocurrió un error inesperado al registrar"))
            }
            .collect { result ->
                when (result) {
                    is Resource.Success -> emit(RegisterResult.Success)
                    is Resource.Error -> emit(RegisterResult.Error(result.message ?: "Ocurrió un error en el registro"))
                    else -> {}
                }
            }
    }
}

sealed class RegisterResult {
    object Success : RegisterResult()
    data class Error(val message: String) : RegisterResult()
    object Loading : RegisterResult()
    data class ValidationError(
        val nombreError: String? = null,
        val emailError: String? = null,
        val passwordError: String? = null
    ) : RegisterResult()
}