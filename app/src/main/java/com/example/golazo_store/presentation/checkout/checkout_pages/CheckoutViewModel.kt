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
import kotlinx.coroutines.delay
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

    fun refreshCurrentSelections() {
        viewModelScope.launch {
            launch {
                direccionRepository.getDirecciones().collect { res ->
                    if (res is Resource.Success) {
                        val direcciones = res.data ?: emptyList()
                        val currentPrincipalId = _state.value.direccionPrincipal?.id
                        val fallback = direcciones.find { it.id == currentPrincipalId } ?: direcciones.find { it.esPrincipal } ?: direcciones.firstOrNull()
                        _state.update { it.copy(direccionPrincipal = fallback, allDirecciones = direcciones) }
                    }
                }
            }
            launch {
                metodoPagoRepository.getMetodosPago().collect { res ->
                    if (res is Resource.Success) {
                        val metodos = res.data ?: emptyList()
                        val currentPrincipalId = _state.value.metodoPagoPrincipal?.id
                        val fallback = metodos.find { it.id == currentPrincipalId } ?: metodos.find { it.esPrincipal } ?: metodos.firstOrNull()
                        _state.update { it.copy(metodoPagoPrincipal = fallback, allMetodosPago = metodos) }
                    }
                }
            }
        }
    }

    fun selectAddress(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingData = true) }
            
            direccionRepository.getDirecciones().collect { res ->
                if (res is Resource.Success) {
                    val direcciones = res.data ?: emptyList()
                    val selected = direcciones.find { it.id == id }
                    if (selected != null) {
                        _state.update { it.copy(direccionPrincipal = selected, allDirecciones = direcciones) }
                    } else {
                        // Si no lo encuentra por alguna razon, mantiene el actual o usa principal
                        val currentPrincipalId = _state.value.direccionPrincipal?.id
                        val fallback = direcciones.find { it.id == currentPrincipalId } ?: direcciones.find { it.esPrincipal } ?: direcciones.firstOrNull()
                        _state.update { it.copy(direccionPrincipal = fallback, allDirecciones = direcciones) }
                    }
                }
            }
            
            delay(500)
            _state.update { it.copy(isLoadingData = false) }
        }
    }

    fun selectPayment(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingData = true) }
            
            metodoPagoRepository.getMetodosPago().collect { res ->
                if (res is Resource.Success) {
                    val metodos = res.data ?: emptyList()
                    val selected = metodos.find { it.id == id }
                    if (selected != null) {
                        _state.update { it.copy(metodoPagoPrincipal = selected, allMetodosPago = metodos) }
                    } else {
                        val currentPrincipalId = _state.value.metodoPagoPrincipal?.id
                        val fallback = metodos.find { it.id == currentPrincipalId } ?: metodos.find { it.esPrincipal } ?: metodos.firstOrNull()
                        _state.update { it.copy(metodoPagoPrincipal = fallback, allMetodosPago = metodos) }
                    }
                }
            }
            
            delay(500)
            _state.update { it.copy(isLoadingData = false) }
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
