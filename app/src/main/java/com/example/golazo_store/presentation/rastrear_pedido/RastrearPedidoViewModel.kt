package com.example.golazo_store.presentation.rastrear_pedido

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.repository.PedidoRepository
import com.example.golazo_store.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RastrearPedidoViewModel @Inject constructor(
    private val repository: PedidoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val pedidoId: Int = savedStateHandle.get<Int>("pedidoId") ?: -1

    private val _state = MutableStateFlow(RastrearPedidoState(isLoading = true))
    val state: StateFlow<RastrearPedidoState> = _state.asStateFlow()

    init {
        if (pedidoId != -1) {
            loadPedido(pedidoId)
        } else {
            _state.update { it.copy(isLoading = false, error = "ID inválido") }
        }
    }

    fun loadPedido(id: Int) {
        viewModelScope.launch {
            repository.getAdminPedidoById(id).collect { resource -> // Reusing existing endpoint logic as it returns same format
                when (resource) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update { 
                        it.copy(isLoading = false, pedido = resource.data, error = null) 
                    }
                    is Resource.Error -> _state.update { 
                        it.copy(isLoading = false, error = resource.message ?: "Error desconocido") 
                    }
                }
            }
        }
    }
}
