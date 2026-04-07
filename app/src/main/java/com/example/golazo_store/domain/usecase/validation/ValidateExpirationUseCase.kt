package com.example.golazo_store.domain.usecase.validation

import javax.inject.Inject

class ValidateExpirationUseCase @Inject constructor() {
    operator fun invoke(expiration: String): ValidationResult {
        val value = expiration.filter { it.isDigit() }
        if (value.length != 4) {
            return ValidationResult(
                successful = false,
                errorMessage = "Expiración inválida (MM/AA)"
            )
        }
        val month = value.take(2).toIntOrNull() ?: 0
        if (month < 1 || month > 12) {
            return ValidationResult(
                successful = false,
                errorMessage = "Mes inválido"
            )
        }
        return ValidationResult(successful = true)
    }
}
