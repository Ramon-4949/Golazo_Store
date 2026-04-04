package com.example.golazo_store.presentation.payment.add

sealed class PaymentAddEvent {
    data class OnNombreTitularChange(val value: String) : PaymentAddEvent()
    data class OnNumeroTarjetaChange(val value: String) : PaymentAddEvent() // Max length 16 digits
    data class OnExpiracionChange(val value: String) : PaymentAddEvent() // Max length 5 ("MM/YY")
    data class OnCvvChange(val value: String) : PaymentAddEvent() // Max length 3
    data class OnEsPrincipalChange(val value: Boolean) : PaymentAddEvent()
    object OnSave : PaymentAddEvent()
}
