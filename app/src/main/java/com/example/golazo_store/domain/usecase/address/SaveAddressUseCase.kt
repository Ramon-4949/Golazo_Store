package com.example.golazo_store.domain.usecase.address

import com.example.golazo_store.domain.model.Direccion
import com.example.golazo_store.domain.repository.DireccionRepository
import com.example.golazo_store.domain.usecase.validation.ValidateEmptyFieldUseCase
import com.example.golazo_store.domain.utils.Resource
import javax.inject.Inject

class SaveAddressUseCase @Inject constructor(
    private val repository: DireccionRepository,
    private val validateEmptyField: ValidateEmptyFieldUseCase
) {
    suspend operator fun invoke(
        id: Int?,
        usuarioId: Int,
        nombreDireccion: String,
        calleNumero: String,
        provincia: String,
        codigoPostal: String,
        ciudad: String,
        reference: String,
        esPrincipal: Boolean
    ): SaveAddressResult {
        val nombreResult = validateEmptyField(nombreDireccion, "nombre de dirección")
        val calleResult = validateEmptyField(calleNumero, "calle")
        val provinciaResult = validateEmptyField(provincia, "provincia")
        val cpResult = validateEmptyField(codigoPostal, "código postal")
        val ciudadResult = validateEmptyField(ciudad, "ciudad")

        val hasError = listOf(
            nombreResult, calleResult, provinciaResult, cpResult, ciudadResult
        ).any { !it.successful }

        if (hasError) {
            return SaveAddressResult.ValidationError(
                nombreError = nombreResult.errorMessage,
                calleError = calleResult.errorMessage,
                provinciaError = provinciaResult.errorMessage,
                codigoPostalError = cpResult.errorMessage,
                ciudadError = ciudadResult.errorMessage
            )
        }

        val direccion = Direccion(
            id = id ?: 0,
            usuarioId = usuarioId,
            nombreDireccion = nombreDireccion,
            calleNumero = calleNumero,
            provincia = provincia,
            codigoPostal = codigoPostal,
            ciudad = ciudad,
            reference = reference,
            esPrincipal = esPrincipal
        )

        val result = if (id != null && id != -1) {
            repository.updateDireccion(id, direccion)
        } else {
            repository.createDireccion(direccion)
        }

        return when (result) {
            is Resource.Success -> SaveAddressResult.Success
            is Resource.Error -> SaveAddressResult.Error(result.message ?: "Ocurrió un error inesperado")
            else -> SaveAddressResult.Error("Estado inesperado")
        }
    }
}

sealed class SaveAddressResult {
    object Success : SaveAddressResult()
    data class Error(val message: String) : SaveAddressResult()
    data class ValidationError(
        val nombreError: String? = null,
        val calleError: String? = null,
        val provinciaError: String? = null,
        val codigoPostalError: String? = null,
        val ciudadError: String? = null
    ) : SaveAddressResult()
}
