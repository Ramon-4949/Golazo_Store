package com.example.golazo_store.data.repository

import app.cash.turbine.test
import com.example.golazo_store.data.local.dao.CartDao
import com.example.golazo_store.data.local.entity.CartEntity
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CartRepositoryImplTest {

    @MockK
    private lateinit var cartDao: CartDao

    private lateinit var repository: CartRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = CartRepositoryImpl(cartDao)
    }

    @Test
    fun `addToCart inserta un nuevo producto en la base de datos local exitosamente`() = runTest {
        val camisetaId = 1
        val talla = "M"
        val cantidad = 2

        coEvery { cartDao.getCartItem(camisetaId, talla) } returns null
        coEvery { cartDao.insertCartItem(any()) } just Runs

        repository.addToCart(camisetaId, talla, cantidad)

        coVerify(exactly = 1) { cartDao.insertCartItem(any()) }
    }

    @Test
    fun `removeFromCart elimina el producto de la base de datos local`() = runTest {
        val idCarrito = 5

        coEvery { cartDao.deleteCartItem(idCarrito) } just Runs

        repository.removeFromCart(idCarrito)

        coVerify(exactly = 1) { cartDao.deleteCartItem(idCarrito) }
    }
}