package com.example.golazo_store.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CartUiState(isLoading = true))
    val state: StateFlow<CartUiState> = _state.asStateFlow()

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            cartRepository.getCartItems().collect { items ->
                val subtotal = items.sumOf { it.precio * it.cantidad }
                _state.update {
                    it.copy(
                        items = items,
                        isLoading = false,
                        subtotal = subtotal,
                        total = subtotal // Shipping is free according to the design
                    )
                }
            }
        }
    }

    fun onEvent(event: CartEvent) {
        when (event) {
            is CartEvent.IncrementQuantity -> {
                viewModelScope.launch {
                    cartRepository.updateQuantity(event.cartId, event.currentQuantity + 1)
                }
            }
            is CartEvent.DecrementQuantity -> {
                viewModelScope.launch {
                    if (event.currentQuantity > 1) {
                        cartRepository.updateQuantity(event.cartId, event.currentQuantity - 1)
                    } else {
                        cartRepository.removeFromCart(event.cartId)
                    }
                }
            }
            is CartEvent.RemoveItem -> {
                viewModelScope.launch {
                    cartRepository.removeFromCart(event.cartId)
                }
            }
            is CartEvent.Checkout -> {
                _state.update { it.copy(errorMessage = "checkout") }
            }
        }
    }
}
