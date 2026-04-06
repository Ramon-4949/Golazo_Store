package com.example.golazo_store.presentation.categories

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

import com.example.golazo_store.domain.repository.CartRepository

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CategoriesUiState())
    val state: StateFlow<CategoriesUiState> = _state.asStateFlow()

    init {
        loadInitialData()
        observeCart()
    }

    fun onEvent(event: CategoriesEvent) {
        when (event) {
            is CategoriesEvent.UpdateSearchQuery -> {
                _state.update { it.copy(searchQuery = event.query) }
            }
            is CategoriesEvent.ClickCategory -> {
                // Future Implementation: Navigate to specific category list
            }
            CategoriesEvent.ClickMenu -> { }
            CategoriesEvent.ClickCart -> { }
            CategoriesEvent.ClickViewAll -> { }
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
            _state.update { it.copy(isLoading = true) }

            val mockCategories = listOf(
                CategoryDemo("Entrenamiento", "100% flexibles", com.example.Golazo_Store.R.drawable.entrenamiento),
                CategoryDemo("Selecciones", "Orgullo nacional", com.example.Golazo_Store.R.drawable.selecciones),
                CategoryDemo("Clubes", "Equipos de ligas top", com.example.Golazo_Store.R.drawable.clubes)
            )

            _state.update {
                it.copy(
                    isLoading = false,
                    categories = mockCategories
                )
            }
        }
    }
}


