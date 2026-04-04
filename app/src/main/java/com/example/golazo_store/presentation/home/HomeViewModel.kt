package com.example.golazo_store.presentation.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.repository.CamisetaRepository
import com.example.golazo_store.domain.repository.CartRepository
import com.example.golazo_store.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val camisetaRepository: CamisetaRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadInitialData()
        observeCart()
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
                // To be implemented later via DB extension
            }
            is HomeEvent.AddToCart -> {
                viewModelScope.launch {
                    cartRepository.addToCart(event.id, "M", 1)
                }
            }
            HomeEvent.ClickMenu -> { /* Future drawer state trigger */ }
            HomeEvent.ClickCart -> { /* Future cart navigation trigger */ }
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartRepository.getCartItems().collect { items ->
                val totalCount = items.sumOf { it.cantidad }
                _state.update { it.copy(cartItemCount = totalCount) }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            camisetaRepository.getCamisetas().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                products = resource.data ?: emptyList(),
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(isLoading = false, error = resource.message)
                        }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }
}


