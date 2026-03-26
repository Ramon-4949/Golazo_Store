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

@HiltViewModel
class CategoriesViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(CategoriesUiState())
    val state: StateFlow<CategoriesUiState> = _state.asStateFlow()

    init {
        loadInitialData()

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

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val mockCategories = listOf(
                CategoryDemo("Clubes", "Equipos de ligas top", Color(0xFF1E2836)),
                CategoryDemo("Selecciones", "Orgullo nacional", Color(0xFF111418)),
                CategoryDemo("Retro Clásicos", "Las leyendas nunca mueren", Color(0xFF1B3B26), badge = "COLECCIONISTA"),
                CategoryDemo("Accesorios", "Completa tu estilo", Color(0xFF4A2511))
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
