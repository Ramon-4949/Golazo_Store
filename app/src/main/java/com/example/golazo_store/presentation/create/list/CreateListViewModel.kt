package com.example.golazo_store.presentation.create.list

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
class CreateListViewModel @Inject constructor(
    private val repository: CamisetaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateListUiState())
    val state: StateFlow<CreateListUiState> = _state.asStateFlow()

    init {
        // Quitamos la inyección fantasma de Antigravity
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

    fun onEvent(event: CreateListEvent) {
        when (event) {
            is CreateListEvent.OnDeleteClick -> {
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