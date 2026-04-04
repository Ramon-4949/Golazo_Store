package com.example.golazo_store.presentation.admin_pedidos

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
class AdminPedidosViewModel @Inject constructor(
    private val repository: PedidoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminPedidosUiState())
    val state: StateFlow<AdminPedidosUiState> = _state.asStateFlow()

    init {
        loadPedidos()
    }

    fun onEvent(event: AdminPedidosEvent) {
        when (event) {
            is AdminPedidosEvent.SelectFilter -> {
                _state.update { it.copy(selectedFilter = event.filter) }
                applyFilter()
            }
            is AdminPedidosEvent.LoadPedidos -> {
                loadPedidos()
            }
        }
    }

    private fun loadPedidos() {
        viewModelScope.launch {
            repository.getAdminPedidos().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                pedidos = resource.data ?: emptyList(),
                                error = null
                            )
                        }
                        applyFilter()
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = resource.message ?: "Error desconocido al cargar pedidos"
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private fun applyFilter() {
        val currentPedidos = _state.value.pedidos
        val filter = _state.value.selectedFilter

        val filteredList = if (filter == "Todos") {
            currentPedidos
        } else {
            currentPedidos.filter { it.estado.equals(filter, ignoreCase = true) }
        }

        _state.update { it.copy(filteredPedidos = filteredList) }
    }
}
