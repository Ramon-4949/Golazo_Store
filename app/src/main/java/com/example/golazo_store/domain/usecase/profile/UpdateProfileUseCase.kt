package com.example.golazo_store.domain.usecase.profile

import com.example.golazo_store.domain.repository.AuthRepository
import com.example.golazo_store.domain.usecase.validation.ValidateEmptyFieldUseCase
import com.example.golazo_store.domain.usecase.validation.ValidatePasswordUseCase
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val validateEmptyField: ValidateEmptyFieldUseCase,
    private val validatePassword: ValidatePasswordUseCase
) {
    operator fun invoke(
        id: Int,
        nombreUsuario: String,
        nuevaContrasena: String,
        confirmarContrasena: String
    ): Flow<UpdateProfileResult> = flow {
        val nombreResult = validateEmptyField(nombreUsuario, "nombre de usuario")
        var passwordResultError: String? = null
        var confirmPasswordError: String? = null

        if (nuevaContrasena.isNotBlank() || confirmarContrasena.isNotBlank()) {
            val passRes = validatePassword(nuevaContrasena)
            if (!passRes.successful) {
                passwordResultError = passRes.errorMessage
            }

            if (nuevaContrasena != confirmarContrasena) {
                confirmPasswordError = "Las contraseñas no coinciden"
            }
        }

        if (!nombreResult.successful || passwordResultError != null || confirmPasswordError != null) {
            emit(UpdateProfileResult.ValidationError(
                nombreError = nombreResult.errorMessage,
                passwordError = passwordResultError,
                confirmPasswordError = confirmPasswordError
            ))
            return@flow
        }

        emit(UpdateProfileResult.Loading)

        val contrasena = if (nuevaContrasena.isNotBlank()) nuevaContrasena else null
        authRepository.updateProfile(id, nombreUsuario, contrasena).collect { result ->
            when (result) {
                is Resource.Success -> emit(UpdateProfileResult.Success(result.data ?: "Perfil actualizado"))
                is Resource.Error -> emit(UpdateProfileResult.Error(result.message ?: "Ocurrió un error"))
                else -> {}
            }
        }
    }
}

sealed class UpdateProfileResult {
    data class Success(val message: String) : UpdateProfileResult()
    data class Error(val message: String) : UpdateProfileResult()
    object Loading : UpdateProfileResult()
    data class ValidationError(
        val nombreError: String? = null,
        val passwordError: String? = null,
        val confirmPasswordError: String? = null
    ) : UpdateProfileResult()
}
