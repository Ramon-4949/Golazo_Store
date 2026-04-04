package com.example.golazo_store.presentation.favorites

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.model.Camiseta
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
                    val updatedItems = currentState.favoriteItems.filterNot { it.id == event.id }
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

            val mockFavorites = emptyList<Camiseta>()

            _state.update {
                it.copy(
                    isLoading = false,
                    favoriteItems = mockFavorites
                )
            }
        }
    }
}


