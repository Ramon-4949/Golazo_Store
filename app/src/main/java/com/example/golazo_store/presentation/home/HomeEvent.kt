package com.example.golazo_store.presentation.home

sealed interface HomeEvent {
    data class UpdateSearch(val query: String) : HomeEvent
    data class SelectFilter(val filter: String) : HomeEvent
    data class ToggleFavorite(val productName: String) : HomeEvent
    data class AddToCart(val productName: String) : HomeEvent
    object ClickMenu : HomeEvent
    object ClickCart : HomeEvent
}
