package com.example.golazo_store.presentation.mis_pedidos

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
class MisPedidosViewModel @Inject constructor(
    private val repository: PedidoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MisPedidosState(isLoading = true))
    val state: StateFlow<MisPedidosState> = _state.asStateFlow()

    init {
        loadMisPedidos()
    }

    fun loadMisPedidos() {
        viewModelScope.launch {
            repository.getMisPedidos().collect { resource ->
                when (resource) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update { 
                        it.copy(isLoading = false, pedidos = resource.data ?: emptyList(), error = null) 
                    }
                    is Resource.Error -> _state.update { 
                        it.copy(isLoading = false, error = resource.message ?: "Error desconocido") 
                    }
                }
            }
        }
    }

    fun setTab(index: Int) {
        _state.update { it.copy(currentTab = index) }
    }
}
