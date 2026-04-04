package com.example.golazo_store.presentation.favorites

sealed interface FavoritesEvent {
    data class RemoveFavorite(val id: Int) : FavoritesEvent
    data class AddToCart(val id: Int) : FavoritesEvent
    object ClickFilter : FavoritesEvent
    object ClickMenu : FavoritesEvent
    object ClickCart : FavoritesEvent

}

