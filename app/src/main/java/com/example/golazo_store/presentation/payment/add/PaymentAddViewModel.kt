package com.example.golazo_store.presentation.payment.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.usecase.payment.AddPaymentMethodUseCase
import com.example.golazo_store.domain.usecase.payment.AddPaymentResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentAddViewModel @Inject constructor(
    private val addPaymentMethodUseCase: AddPaymentMethodUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentAddUiState())
    val state: StateFlow<PaymentAddUiState> = _state.asStateFlow()

    fun onEvent(event: PaymentAddEvent) {
        when (event) {
            is PaymentAddEvent.OnNombreTitularChange -> _state.update { it.copy(nombreTitular = event.value, titularError = null) }
            is PaymentAddEvent.OnNumeroTarjetaChange -> {
                val digitsOnly = event.value.filter { it.isDigit() }
                if (digitsOnly.length <= 16) {
                    _state.update { it.copy(numeroTarjeta = digitsOnly, numeroTarjetaError = null) }
                }
            }
            is PaymentAddEvent.OnExpiracionChange -> {
                val value = event.value.filter { it.isDigit() }
                if (value.length <= 4) {
                    _state.update { it.copy(expiracionMMYY = value, expiracionError = null) }
                }
            }
            is PaymentAddEvent.OnCvvChange -> {
                val digitsOnly = event.value.filter { it.isDigit() }
                if (digitsOnly.length <= 4) {
                    _state.update { it.copy(cvv = digitsOnly, cvvError = null) }
                }
            }
            is PaymentAddEvent.OnEsPrincipalChange -> _state.update { it.copy(esPrincipal = event.value) }
            is PaymentAddEvent.OnSave -> savePayment()
        }
    }

    private fun savePayment() {
        val currentState = _state.value

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = addPaymentMethodUseCase(
                titular = currentState.nombreTitular,
                numeroTarjeta = currentState.numeroTarjeta,
                expiracion = currentState.expiracionMMYY,
                cvv = currentState.cvv,
                esPrincipal = currentState.esPrincipal
            )

            when (result) {
                is AddPaymentResult.Success -> {
                    _state.update { it.copy(isLoading = false, isSaved = true) }
                }
                is AddPaymentResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is AddPaymentResult.ValidationError -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            titularError = result.titularError,
                            numeroTarjetaError = result.numeroTarjetaError,
                            expiracionError = result.expiracionError,
                            cvvError = result.cvvError
                        )
                    }
                }
            }
        }
    }
}
