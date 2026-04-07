package com.example.golazo_store.domain.usecase.cart

import com.example.golazo_store.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(cartId: Int) {
        repository.removeFromCart(cartId)
    }
}
