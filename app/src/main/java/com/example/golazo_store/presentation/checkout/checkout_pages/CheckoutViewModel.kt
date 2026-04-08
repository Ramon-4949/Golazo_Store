package com.example.golazo_store.presentation.checkout.checkout_pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.model.DetallePedido
import com.example.golazo_store.domain.model.PedidoRegistro
import com.example.golazo_store.domain.repository.CartRepository
import com.example.golazo_store.domain.repository.DireccionRepository
import com.example.golazo_store.domain.repository.MetodoPagoRepository
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
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val direccionRepository: DireccionRepository,
    private val metodoPagoRepository: MetodoPagoRepository,
    private val pedidoRepository: PedidoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutUiState())
    val state: StateFlow<CheckoutUiState> = _state.asStateFlow()

    init {
        loadCheckoutData()
    }

    private fun loadCheckoutData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingData = true) }

            // 1. Cargar el carrito
            cartRepository.getCartItems().collect { items ->
                val subtotal = items.sumOf { it.precio * it.cantidad }
                _state.update {
                    it.copy(
                        cartItems = items,
                        subtotal = subtotal,
                        total = subtotal
                    )
                }
                
                // 2. Cargar dirección principal
                launch {
                    direccionRepository.getDirecciones().collect { res ->
                        if (res is Resource.Success) {
                            val direcciones = res.data ?: emptyList()
                            val currentPrincipalId = _state.value.direccionPrincipal?.id
                            val principal = if (currentPrincipalId != null) {
                                direcciones.find { it.id == currentPrincipalId }
                            } else {
                                direcciones.find { it.esPrincipal } ?: direcciones.firstOrNull()
                            }
                            _state.update { it.copy(direccionPrincipal = principal, allDirecciones = direcciones) }
                        }
                    }
                }

                // 3. Cargar método de pago principal
                launch {
                    metodoPagoRepository.getMetodosPago().collect { res ->
                        if (res is Resource.Success) {
                            val metodos = res.data ?: emptyList()
                            val currentPrincipalId = _state.value.metodoPagoPrincipal?.id
                            val principal = if (currentPrincipalId != null) {
                                metodos.find { it.id == currentPrincipalId }
                            } else {
                                metodos.find { it.esPrincipal } ?: metodos.firstOrNull()
                            }
                            _state.update { it.copy(metodoPagoPrincipal = principal, allMetodosPago = metodos) }
                        }
                    }
                }

                _state.update { it.copy(isLoadingData = false) }
            }
        }
    }

    fun selectAddress(id: Int) {
        val selected = _state.value.allDirecciones.find { it.id == id }
        if (selected != null) {
            _state.update { it.copy(direccionPrincipal = selected) }
        }
    }

    fun selectPayment(id: Int) {
        val selected = _state.value.allMetodosPago.find { it.id == id }
        if (selected != null) {
            _state.update { it.copy(metodoPagoPrincipal = selected) }
        }
    }

    fun onConfirmOrder() {
        val currentState = _state.value
        
        if (currentState.direccionPrincipal == null) {
            _state.update { it.copy(error = "Por favor selecciona una dirección de envío.") }
            return
        }
        
        if (currentState.metodoPagoPrincipal == null) {
            _state.update { it.copy(error = "Por favor selecciona un método de pago.") }
            return
        }
        
        if (currentState.cartItems.isEmpty()) {
            _state.update { it.copy(error = "Tu carrito está vacío.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isPlacingOrder = true, error = null) }

            val items = currentState.cartItems.map {
                DetallePedido(
                    camisetaId = it.camisetaId,
                    cantidad = it.cantidad,
                    talla = it.talla
                )
            }

            val request = PedidoRegistro(
                direccionId = currentState.direccionPrincipal.id,
                metodoPagoId = currentState.metodoPagoPrincipal.id ?: 0,
                items = items
            )

            val result = pedidoRepository.createPedido(request)

            if (result is Resource.Success) {
                cartRepository.clearCart()
                _state.update { 
                    it.copy(
                        isPlacingOrder = false, 
                        createdOrderNumber = result.data?.numeroPedido ?: "N/A"
                    ) 
                }
            } else {
                _state.update { 
                    it.copy(
                        isPlacingOrder = false, 
                        error = result.message ?: "Error al procesar la compra."
                    ) 
                }
            }
        }
    }
    
    fun dismissError() {
        _state.update { it.copy(error = null) }
    }
}
