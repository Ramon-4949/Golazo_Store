package com.example.golazo_store.presentation.payment.list

sealed class PaymentListEvent {
    data class OnDeletePayment(val id: Int) : PaymentListEvent()
    object LoadPayments : PaymentListEvent()
}
