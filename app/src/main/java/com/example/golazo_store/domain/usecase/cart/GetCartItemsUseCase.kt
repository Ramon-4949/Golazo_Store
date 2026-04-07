package com.example.golazo_store.domain.usecase.cart

import com.example.golazo_store.domain.model.CartItem
import com.example.golazo_store.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartItemsUseCase @Inject constructor(
    private val repository: CartRepository
) {
    operator fun invoke(): Flow<List<CartItem>> {
        return repository.getCartItems()
    }
}
