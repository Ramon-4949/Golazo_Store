package com.example.golazo_store.data.repository

import com.example.golazo_store.data.local.dao.CartDao
import com.example.golazo_store.data.local.entity.CartEntity
import com.example.golazo_store.domain.model.CartItem
import com.example.golazo_store.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepositoryImpl(
    private val cartDao: CartDao
) : CartRepository {

    override fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getCartItems().map { list ->
            list.map { itemWithCamiseta ->
                CartItem(
                    cartId = itemWithCamiseta.cartItem.id,
                    camisetaId = itemWithCamiseta.camiseta.id,
                    nombre = itemWithCamiseta.camiseta.nombre,
                    precio = itemWithCamiseta.camiseta.precio,
                    imagenUrl = itemWithCamiseta.camiseta.imagenUrl,
                    talla = itemWithCamiseta.cartItem.talla,
                    cantidad = itemWithCamiseta.cartItem.cantidad
                )
            }
        }
    }

    override suspend fun addToCart(camisetaId: Int, talla: String, cantidad: Int) {
        val existingItem = cartDao.getCartItem(camisetaId, talla)
        if (existingItem != null) {
            cartDao.updateQuantity(existingItem.id, existingItem.cantidad + cantidad)
        } else {
            cartDao.insertCartItem(
                CartEntity(
                    camisetaId = camisetaId,
                    talla = talla,
                    cantidad = cantidad
                )
            )
        }
    }

    override suspend fun updateQuantity(cartId: Int, cantidad: Int) {
        if (cantidad > 0) {
            cartDao.updateQuantity(cartId, cantidad)
        } else {
            cartDao.deleteCartItem(cartId)
        }
    }

    override suspend fun removeFromCart(cartId: Int) {
        cartDao.deleteCartItem(cartId)
    }

    override suspend fun clearCart() {
        cartDao.clearCart()
    }
}
