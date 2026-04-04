package com.example.golazo_store.presentation.payment.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.model.MetodoPagoRegistro
import com.example.golazo_store.domain.repository.MetodoPagoRepository
import com.example.golazo_store.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentAddViewModel @Inject constructor(
    private val repository: MetodoPagoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentAddUiState())
    val state: StateFlow<PaymentAddUiState> = _state.asStateFlow()

    fun onEvent(event: PaymentAddEvent) {
        when (event) {
            is PaymentAddEvent.OnNombreTitularChange -> _state.update { it.copy(nombreTitular = event.value) }
            is PaymentAddEvent.OnNumeroTarjetaChange -> {
                val digitsOnly = event.value.filter { it.isDigit() }
                if (digitsOnly.length <= 16) {
                    _state.update { it.copy(numeroTarjeta = digitsOnly) }
                }
            }
            is PaymentAddEvent.OnExpiracionChange -> {
                val value = event.value.filter { it.isDigit() }
                if (value.length <= 4) {
                    _state.update { it.copy(expiracionMMYY = value) }
                }
            }
            is PaymentAddEvent.OnCvvChange -> {
                val digitsOnly = event.value.filter { it.isDigit() }
                if (digitsOnly.length <= 4) {
                    _state.update { it.copy(cvv = digitsOnly) }
                }
            }
            is PaymentAddEvent.OnEsPrincipalChange -> _state.update { it.copy(esPrincipal = event.value) }
            is PaymentAddEvent.OnSave -> savePayment()
        }
    }

    private fun savePayment() {
        val currentState = _state.value
        
        // Basic validations
        if (currentState.nombreTitular.isBlank() || currentState.numeroTarjeta.length < 15 ||
            currentState.expiracionMMYY.length != 4 || currentState.cvv.length < 3
        ) {
            _state.update { it.copy(error = "Por favor completa todos los campos correctamente.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val metodoPagoRegistro = MetodoPagoRegistro(
                titular = currentState.nombreTitular,
                numeroTarjeta = currentState.numeroTarjeta,
                expiracion = "${currentState.expiracionMMYY.take(2)}/${currentState.expiracionMMYY.takeLast(2)}",
                cvv = currentState.cvv,
                esPrincipal = currentState.esPrincipal
            )

            val result = repository.createMetodoPago(metodoPagoRegistro)

            if (result is Resource.Success) {
                _state.update { it.copy(isLoading = false, isSaved = true) }
            } else if (result is Resource.Error) {
                _state.update { it.copy(isLoading = false, error = result.message ?: "Error al guardar la tarjeta") }
            }
        }
    }
}
