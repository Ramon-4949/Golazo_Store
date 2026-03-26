package com.example.golazo_store.presentation.favorites

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.presentation.home.HomeScreen.ProductDemo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(FavoritesUiState())
    val state: StateFlow<FavoritesUiState> = _state.asStateFlow()

    init {
        loadInitialFavorites()
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.RemoveFavorite -> {
                _state.update { currentState ->
                    val updatedItems = currentState.favoriteItems.filterNot { it.name == event.productName }
                    currentState.copy(favoriteItems = updatedItems)
                }
            }
            is FavoritesEvent.AddToCart -> {
                // Future Implementation: ADD logic
            }
            FavoritesEvent.ClickFilter -> {
                // Future Implementation: Open filter bottom sheet
            }
            FavoritesEvent.ClickMenu -> { }
            FavoritesEvent.ClickCart -> { }
        }
    }

    private fun loadInitialFavorites() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Dummy data matching the favorites screenshot provided by the user
            val mockFavorites = listOf(
                ProductDemo("Argentina Retro", "$3,500", true, Color(0xFF1E242B))
            )

            _state.update {
                it.copy(
                    isLoading = false,
                    favoriteItems = mockFavorites
                )
            }
        }
    }
}
