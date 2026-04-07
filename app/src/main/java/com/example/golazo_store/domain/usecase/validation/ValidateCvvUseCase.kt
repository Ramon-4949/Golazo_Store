package com.example.golazo_store.domain.usecase.validation

import javax.inject.Inject

class ValidateCvvUseCase @Inject constructor() {
    operator fun invoke(cvv: String): ValidationResult {
        if (cvv.length < 3) {
            return ValidationResult(
                successful = false,
                errorMessage = "CVV inválido"
            )
        }
        return ValidationResult(successful = true)
    }
}
