package com.example.golazo_store.data.local.dao

import androidx.room.*
import com.example.golazo_store.data.local.entity.CartEntity
import com.example.golazo_store.data.local.entity.CartItemWithCamiseta
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Transaction
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItemWithCamiseta>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartEntity)

    @Query("SELECT * FROM cart_items WHERE camisetaId = :camisetaId AND talla = :talla LIMIT 1")
    suspend fun getCartItem(camisetaId: Int, talla: String): CartEntity?

    @Query("UPDATE cart_items SET cantidad = :cantidad WHERE id = :cartId")
    suspend fun updateQuantity(cartId: Int, cantidad: Int)

    @Query("DELETE FROM cart_items WHERE id = :cartId")
    suspend fun deleteCartItem(cartId: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}
