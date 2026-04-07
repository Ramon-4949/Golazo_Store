package com.example.golazo_store.domain.usecase.validation

import android.util.Patterns
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {
    operator fun invoke(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "El correo electrónico no puede estar vacío"
            )
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = "El correo electrónico no es válido"
            )
        }
        return ValidationResult(successful = true)
    }
}
