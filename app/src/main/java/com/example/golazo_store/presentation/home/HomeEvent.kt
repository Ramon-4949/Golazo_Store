package com.example.golazo_store.presentation.home

sealed interface HomeEvent {
    data class UpdateSearch(val query: String) : HomeEvent
    data class SelectFilter(val filter: String) : HomeEvent
    data class ToggleFavorite(val id: Int) : HomeEvent
    data class AddToCart(val id: Int) : HomeEvent
    object ClickMenu : HomeEvent
    object ClickCart : HomeEvent

}

