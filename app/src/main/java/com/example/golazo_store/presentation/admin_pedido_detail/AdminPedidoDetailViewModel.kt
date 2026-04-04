package com.example.golazo_store.presentation.admin_pedido_detail

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
class AdminPedidoDetailViewModel @Inject constructor(
    private val repository: PedidoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val pedidoId: Int = savedStateHandle.get<Int>("pedidoId") ?: -1

    private val _state = MutableStateFlow(AdminPedidoDetailUiState(isLoading = true))
    val state: StateFlow<AdminPedidoDetailUiState> = _state.asStateFlow()

    init {
        if (pedidoId != -1) {
            loadPedido(pedidoId)
        } else {
            _state.update { it.copy(isLoading = false, error = "ID de pedido inválido") }
        }
    }

    private fun loadPedido(id: Int) {
        viewModelScope.launch {
            repository.getAdminPedidoById(id).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                pedido = resource.data,
                                selectedEstado = resource.data?.estado ?: "",
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(isLoading = false, error = resource.message ?: "Error al cargar el pedido")
                        }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun onEvent(event: AdminPedidoDetailEvent) {
        when (event) {
            is AdminPedidoDetailEvent.SelectEstado -> {
                _state.update { it.copy(selectedEstado = event.estado) }
            }
            is AdminPedidoDetailEvent.SaveChanges -> {
                if (pedidoId != -1) {
                    updateEstado()
                }
            }
            is AdminPedidoDetailEvent.RetryLoading -> {
                if (pedidoId != -1) loadPedido(pedidoId)
            }
        }
    }

    private fun updateEstado() {
        val newEstado = _state.value.selectedEstado
        _state.update { it.copy(isUpdating = true, error = null) }
        viewModelScope.launch {
            val resource = repository.updatePedidoEstado(pedidoId, newEstado)
            when (resource) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isUpdating = false,
                            updateSuccess = true,
                            pedido = it.pedido?.copy(estado = newEstado)
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isUpdating = false,
                            updateSuccess = false,
                            error = resource.message ?: "No se pudo actualizar el estado"
                        )
                    }
                }
                is Resource.Loading -> { }
            }
        }
    }
}
