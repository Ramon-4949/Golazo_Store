package com.example.golazo_store.presentation.detail

import androidx.lifecycle.SavedStateHandle
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
class CamisetaDetailViewModel @Inject constructor(
    private val camisetaRepository: CamisetaRepository,
    private val cartRepository: CartRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val currentCamisetaId: Int = savedStateHandle.get<Int>("id") ?: -1

    private val _state = MutableStateFlow(CamisetaDetailUiState(isLoading = true))
    val state: StateFlow<CamisetaDetailUiState> = _state.asStateFlow()

    init {
        if (currentCamisetaId != -1) {
            loadCamiseta(currentCamisetaId)
        } else {
            _state.update { it.copy(isLoading = false, errorMessage = "ID de producto inválido") }
        }
    }

    private fun loadCamiseta(id: Int) {
        viewModelScope.launch {
            camisetaRepository.getCamisetaById(id).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                camiseta = resource.data,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(isLoading = false, errorMessage = resource.message ?: "Error al cargar datos")
                        }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun onEvent(event: CamisetaDetailEvent) {
        when (event) {
            is CamisetaDetailEvent.SelectSize -> {
                _state.update { it.copy(selectedSize = event.size) }
            }
            is CamisetaDetailEvent.AddToCart -> {
                val size = _state.value.selectedSize
                if (size == null) {
                    _state.update { it.copy(errorMessage = "Debes seleccionar una talla primero") }
                } else {
                    viewModelScope.launch {
                        _state.update { it.copy(isLoading = true) }
                        cartRepository.addToCart(event.id, size, 1)
                        _state.update { it.copy(isLoading = false, isAddedToCart = true, errorMessage = null) }
                    }
                }
            }
            is CamisetaDetailEvent.ResetAddToCart -> {
                _state.update { it.copy(isAddedToCart = false) }
            }
            is CamisetaDetailEvent.RetryLoading -> {
                if (currentCamisetaId != -1) {
                    loadCamiseta(currentCamisetaId)
                }
            }
        }
    }
}
