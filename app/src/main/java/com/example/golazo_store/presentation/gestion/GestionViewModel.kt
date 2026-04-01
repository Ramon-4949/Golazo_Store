package com.example.golazo_store.presentation.gestion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.repository.CamisetaRepository
import com.example.golazo_store.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestionViewModel @Inject constructor(
    private val repository: CamisetaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GestionUiState())
    val state: StateFlow<GestionUiState> = _state.asStateFlow()

    init {
        loadCamisetas()
    }

    private fun loadCamisetas() {
        viewModelScope.launch {
            repository.getCamisetas().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.update { 
                            it.copy(
                                camisetas = result.data ?: emptyList(),
                                isLoading = false,
                                errorMessage = null
                            ) 
                        }
                    }
                    is Resource.Error -> {
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            ) 
                        }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                }
            }
        }
    }

    fun onEvent(event: GestionEvent) {
        when (event) {
            is GestionEvent.OnDeleteClick -> {
                viewModelScope.launch {
                    val result = repository.deleteCamiseta(event.camiseta.id)
                    if (result is Resource.Error) {
                        _state.update { it.copy(errorMessage = result.message) }
                    }
                }
            }
        }
    }
}
