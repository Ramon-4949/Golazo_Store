package com.example.golazo_store.domain.usecase.payment

import com.example.golazo_store.domain.model.MetodoPagoRegistro
import com.example.golazo_store.domain.repository.MetodoPagoRepository
import com.example.golazo_store.domain.usecase.validation.ValidateCardNumberUseCase
import com.example.golazo_store.domain.usecase.validation.ValidateCvvUseCase
import com.example.golazo_store.domain.usecase.validation.ValidateEmptyFieldUseCase
import com.example.golazo_store.domain.usecase.validation.ValidateExpirationUseCase
import com.example.golazo_store.domain.utils.Resource
import javax.inject.Inject

class AddPaymentMethodUseCase @Inject constructor(
    private val repository: MetodoPagoRepository,
    private val validateEmptyField: ValidateEmptyFieldUseCase,
    private val validateCardNumber: ValidateCardNumberUseCase,
    private val validateExpiration: ValidateExpirationUseCase,
    private val validateCvv: ValidateCvvUseCase
) {
    suspend operator fun invoke(
        titular: String,
        numeroTarjeta: String,
        expiracion: String,
        cvv: String,
        esPrincipal: Boolean
    ): AddPaymentResult {
        val titularResult = validateEmptyField(titular, "nombre del titular")
        val numeroResult = validateCardNumber(numeroTarjeta)
        val expResult = validateExpiration(expiracion)
        val cvvResult = validateCvv(cvv)

        val hasError = listOf(
            titularResult, numeroResult, expResult, cvvResult
        ).any { !it.successful }

        if (hasError) {
            return AddPaymentResult.ValidationError(
                titularError = titularResult.errorMessage,
                numeroTarjetaError = numeroResult.errorMessage,
                expiracionError = expResult.errorMessage,
                cvvError = cvvResult.errorMessage
            )
        }

        val formattedExpiration = "${expiracion.take(2)}/${expiracion.takeLast(2)}"

        val metodoPagoRegistro = MetodoPagoRegistro(
            titular = titular,
            numeroTarjeta = numeroTarjeta,
            expiracion = formattedExpiration,
            cvv = cvv,
            esPrincipal = esPrincipal
        )

        val result = repository.createMetodoPago(metodoPagoRegistro)

        return when (result) {
            is Resource.Success -> AddPaymentResult.Success
            is Resource.Error -> AddPaymentResult.Error(result.message ?: "Error al guardar la tarjeta")
            else -> AddPaymentResult.Error("Estado inesperado")
        }
    }
}

sealed class AddPaymentResult {
    object Success : AddPaymentResult()
    data class Error(val message: String) : AddPaymentResult()
    data class ValidationError(
        val titularError: String? = null,
        val numeroTarjetaError: String? = null,
        val expiracionError: String? = null,
        val cvvError: String? = null
    ) : AddPaymentResult()
}
