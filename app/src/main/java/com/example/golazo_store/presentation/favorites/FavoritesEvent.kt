package com.example.golazo_store.presentation.favorites

sealed interface FavoritesEvent {
    data class RemoveFavorite(val productName: String) : FavoritesEvent
    data class AddToCart(val productName: String) : FavoritesEvent
    object ClickFilter : FavoritesEvent
    object ClickMenu : FavoritesEvent
    object ClickCart : FavoritesEvent
}
