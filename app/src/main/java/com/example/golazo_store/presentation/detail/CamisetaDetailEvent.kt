package com.example.golazo_store.presentation.detail

sealed class CamisetaDetailEvent {
    data class SelectSize(val size: String) : CamisetaDetailEvent()
    data class AddToCart(val id: Int) : CamisetaDetailEvent()
    object RetryLoading : CamisetaDetailEvent()
    object ResetAddToCart : CamisetaDetailEvent()
}
