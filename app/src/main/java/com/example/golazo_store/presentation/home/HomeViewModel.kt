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

import com.example.golazo_store.domain.repository.FavoritesRepository
import com.example.golazo_store.domain.usecase.TriggerSyncFavoritesUseCase

import com.example.golazo_store.domain.repository.CategoriaRepository

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val camisetaRepository: CamisetaRepository,
    private val cartRepository: CartRepository,
    private val favoritesRepository: FavoritesRepository,
    private val categoriaRepository: CategoriaRepository,
    private val triggerSyncFavoritesUseCase: TriggerSyncFavoritesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadCategorias()
        loadInitialData()
        observeCart()
        observeFavorites()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.UpdateSearch -> {
                _state.update { it.copy(searchQuery = event.query) }
                applyFilters()
            }
            is HomeEvent.SelectFilter -> {
                _state.update { it.copy(selectedFilter = event.filter) }
                applyFilters()
            }
            is HomeEvent.ToggleFavorite -> {
                viewModelScope.launch {
                    favoritesRepository.toggleFavorite(event.id)
                    triggerSyncFavoritesUseCase()
                }
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

    private fun applyFilters() {
        _state.update { currentState ->
            val query = currentState.searchQuery.lowercase().trim()
            val filter = currentState.selectedFilter

            val filtered = currentState.allProducts.filter { product ->
                val matchesQuery = if (query.isEmpty()) true else {
                    product.nombre.lowercase().contains(query) || product.descripcion.lowercase().contains(query)
                }

                val productCategoryName = currentState.categoryMap[product.categoriaId] ?: ""

                val matchesCategory = when (filter) {
                    "Todo" -> true
                    "Clubes" -> productCategoryName.contains("Clubes", ignoreCase = true)
                    "Selecciones" -> productCategoryName.contains("Selecciones", ignoreCase = true)
                    "Entrenamiento" -> productCategoryName.contains("Entrenamiento", ignoreCase = true)
                    else -> productCategoryName.equals(filter, ignoreCase = true)
                }

                matchesQuery && matchesCategory
            }

            currentState.copy(products = filtered)
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesRepository.getFavoriteIds().collect { ids ->
                _state.update { it.copy(favoriteIds = ids.toSet()) }
            }
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
                                allProducts = resource.data ?: emptyList(),
                                error = null
                            )
                        }
                        applyFilters()
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

    private fun loadCategorias() {
        viewModelScope.launch {
            categoriaRepository.getCategorias().collect { resource ->
                if (resource is Resource.Success) {
                    val mappings = resource.data?.associate { it.id to it.nombre } ?: emptyMap()
                    _state.update { it.copy(categoryMap = mappings) }
                    applyFilters() // Reapply filters once categories arrive to fix "OTROS" bug
                }
            }
        }
    }
}


