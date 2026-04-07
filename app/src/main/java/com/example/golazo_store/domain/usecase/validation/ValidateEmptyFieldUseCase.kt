package com.example.golazo_store.domain.usecase.validation

import javax.inject.Inject

class ValidateEmptyFieldUseCase @Inject constructor() {
    operator fun invoke(text: String, fieldName: String): ValidationResult {
        if (text.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "El campo $fieldName no puede estar vacío"
            )
        }
        return ValidationResult(successful = true)
    }
}
