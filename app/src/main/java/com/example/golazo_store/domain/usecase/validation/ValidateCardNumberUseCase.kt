package com.example.golazo_store.domain.usecase.validation

import javax.inject.Inject

class ValidateCardNumberUseCase @Inject constructor() {
    operator fun invoke(cardNumber: String): ValidationResult {
        val digitsOnly = cardNumber.filter { it.isDigit() }
        if (digitsOnly.length < 15) {
            return ValidationResult(
                successful = false,
                errorMessage = "Número de tarjeta inválido"
            )
        }
        return ValidationResult(successful = true)
    }
}
