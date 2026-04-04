package com.example.golazo_store.presentation.cart

sealed class CartEvent {
    data class IncrementQuantity(val cartId: Int, val currentQuantity: Int) : CartEvent()
    data class DecrementQuantity(val cartId: Int, val currentQuantity: Int) : CartEvent()
    data class RemoveItem(val cartId: Int) : CartEvent()
    object Checkout : CartEvent()
}
