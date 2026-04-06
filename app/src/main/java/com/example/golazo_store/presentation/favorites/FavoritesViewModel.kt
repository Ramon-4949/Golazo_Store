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


import com.example.golazo_store.domain.repository.FavoritesRepository

import com.example.golazo_store.domain.usecase.TriggerSyncFavoritesUseCase

import com.example.golazo_store.domain.repository.CartRepository

import com.example.golazo_store.domain.repository.CategoriaRepository

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val cartRepository: CartRepository,
    private val categoriaRepository: CategoriaRepository,
    private val triggerSyncFavoritesUseCase: TriggerSyncFavoritesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesUiState())
    val state: StateFlow<FavoritesUiState> = _state.asStateFlow()

    private var favoritesJob: kotlinx.coroutines.Job? = null

    init {
        loadCategorias()
        loadInitialFavorites()
        observeCart()
    }

    fun syncDownFavorites() {
        loadInitialFavorites() // Force UI Flow to reconnect using the new User ID
        viewModelScope.launch {
            favoritesRepository.syncDownFavorites()
        }
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.RemoveFavorite -> {
                viewModelScope.launch {
                    favoritesRepository.toggleFavorite(event.id)
                    triggerSyncFavoritesUseCase()
                }
            }
            is FavoritesEvent.AddToCart -> {
                viewModelScope.launch {
                    cartRepository.addToCart(event.id, "M", 1)
                }
            }
            FavoritesEvent.ClickFilter -> {
                // Future Implementation: Open filter bottom sheet
            }
            FavoritesEvent.ClickMenu -> { }
            FavoritesEvent.ClickCart -> { }
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

    private fun loadInitialFavorites() {
        favoritesJob?.cancel()
        favoritesJob = viewModelScope.launch {
            favoritesRepository.getFavoriteCamisetas().collect { resource ->
                when (resource) {
                    is com.example.golazo_store.domain.utils.Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is com.example.golazo_store.domain.utils.Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                favoriteItems = resource.data ?: emptyList()
                            )
                        }
                    }
                    is com.example.golazo_store.domain.utils.Resource.Error -> {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    private fun loadCategorias() {
        viewModelScope.launch {
            categoriaRepository.getCategorias().collect { resource ->
                if (resource is com.example.golazo_store.domain.utils.Resource.Success) {
                    val mappings = resource.data?.associate { it.id to it.nombre } ?: emptyMap()
                    _state.update { it.copy(categoryMap = mappings) }
                }
            }
        }
    }
}


