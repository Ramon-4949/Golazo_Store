package com.example.golazo_store.presentation.checkout.checkout_pages

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
class CheckoutSuccessViewModel @Inject constructor(
    private val pedidoRepository: PedidoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutSuccessUiState())
    val state: StateFlow<CheckoutSuccessUiState> = _state.asStateFlow()

    init {
        // Obtenemos el orderNumber que pasamos por navegacion en RegistroNavHost
        val initialOrderNumber: String = savedStateHandle.get<String>("orderNumber") ?: ""
        _state.update { it.copy(orderNumber = initialOrderNumber) }
        loadPedidoDetails(initialOrderNumber)
    }

    private fun loadPedidoDetails(orderNumber: String) {
        if (orderNumber.isBlank()) {
            _state.update { it.copy(isLoading = false, error = "Número de pedido inválido") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            pedidoRepository.getMisPedidos().collect { result ->
                if (result is Resource.Success) {
                    val pedidos = result.data ?: emptyList()
                    val match = pedidos.find { it.numeroPedido == orderNumber }
                    if (match != null) {
                        _state.update { it.copy(isLoading = false, pedido = match, error = null) }
                    } else {
                        // Keep loading for a bit just in case it takes time to sync, or show error
                        _state.update { it.copy(isLoading = false, error = "No se encontró el detalle del pedido") }
                    }
                } else if (result is Resource.Error) {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }
}
