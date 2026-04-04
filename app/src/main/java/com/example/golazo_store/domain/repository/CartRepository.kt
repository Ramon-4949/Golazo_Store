package com.example.golazo_store.domain.repository

import com.example.golazo_store.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    suspend fun addToCart(camisetaId: Int, talla: String, cantidad: Int)
    suspend fun updateQuantity(cartId: Int, cantidad: Int)
    suspend fun removeFromCart(cartId: Int)
    suspend fun clearCart()
}
