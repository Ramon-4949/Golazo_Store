package com.example.golazo_store.presentation.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadInitialData()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.UpdateSearch -> {
                _state.update { it.copy(searchQuery = event.query) }
            }
            is HomeEvent.SelectFilter -> {
                _state.update { it.copy(selectedFilter = event.filter) }
            }
            is HomeEvent.ToggleFavorite -> {
                _state.update { currentState ->
                    val updatedProducts = currentState.products.map {
                        if (it.name == event.productName) it.copy(isFavorite = !it.isFavorite) else it
                    }
                    currentState.copy(products = updatedProducts)
                }
            }
            is HomeEvent.AddToCart -> {
                // Future implementation: Add to cart UseCase
            }
            HomeEvent.ClickMenu -> { /* Future drawer state trigger */ }
            HomeEvent.ClickCart -> { /* Future cart navigation trigger */ }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Dummy data to match the current visual state
            val mockData = listOf(
                com.example.golazo_store.presentation.home.HomeScreen.ProductDemo("Argentina Retro", "$3,500", true, Color(0xFF1E242B)),
                com.example.golazo_store.presentation.home.HomeScreen.ProductDemo("Real Madrid Local", "$1,000", false, Color(0xFF262626)),
                com.example.golazo_store.presentation.home.HomeScreen.ProductDemo("Boca Juniors 2024", "$1,500", false, Color(0xFF1E2836)),
                com.example.golazo_store.presentation.home.HomeScreen.ProductDemo("Inter Miami Rosa", "$2,500", false, Color(0xFFFA5774))
            )

            _state.update { 
                it.copy(
                    isLoading = false,
                    products = mockData
                ) 
            }
        }
    }
}
