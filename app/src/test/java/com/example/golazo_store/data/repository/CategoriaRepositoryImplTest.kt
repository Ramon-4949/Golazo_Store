package com.example.golazo_store.data.repository

import app.cash.turbine.test
import com.example.golazo_store.data.remote.api.GolazoApi
import com.example.golazo_store.data.remote.dto.CategoriaDto
import com.example.golazo_store.domain.utils.Resource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class CategoriaRepositoryImplTest {

    @MockK
    private lateinit var api: GolazoApi

    private lateinit var repository: CategoriaRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = CategoriaRepositoryImpl(api)
    }

    @Test
    fun `getCategorias emite Success y la lista de categorias cuando la API responde bien`() = runTest {
        val mockCategoriasDto = listOf(
            CategoriaDto(id = 1, nombre = "Clubes"),
            CategoriaDto(id = 2, nombre = "Selecciones")
        )
        coEvery { api.getCategorias() } returns Response.success(mockCategoriasDto)

        repository.getCategorias().test {
            assertTrue(awaitItem() is Resource.Loading)

            val successItem = awaitItem()
            assertTrue(successItem is Resource.Success)
            assertEquals(2, successItem.data?.size)
            assertEquals("Clubes", successItem.data?.get(0)?.nombre)

            awaitComplete()
        }
    }

    @Test
    fun `getCategorias emite Error cuando falla el internet`() = runTest {
        coEvery { api.getCategorias() } throws Exception("Sin conexión")

        repository.getCategorias().test {
            assertTrue(awaitItem() is Resource.Loading)
            assertTrue(awaitItem() is Resource.Error)
            awaitComplete()
        }
    }
}