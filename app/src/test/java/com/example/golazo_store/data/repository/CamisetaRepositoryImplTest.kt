package com.example.golazo_store.data.repository

import app.cash.turbine.test
import com.example.golazo_store.data.local.dao.CamisetaDao
import com.example.golazo_store.data.local.entity.CamisetaEntity
import com.example.golazo_store.data.remote.dto.CamisetaDto
import com.example.golazo_store.data.remote.remotedatasource.CamisetaRemoteDataSource
import com.example.golazo_store.domain.utils.Resource
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class CamisetaRepositoryImplTest {

    @MockK
    private lateinit var remoteDataSource: CamisetaRemoteDataSource

    @MockK
    private lateinit var localDataSource: CamisetaDao

    private lateinit var repository: CamisetaRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = CamisetaRepositoryImpl(remoteDataSource, localDataSource)
    }

    @Test
    fun `getCamisetaById emite Loading y luego Success cuando la API responde bien`() = runTest {
        val idPrueba = 1

        val mockDto = CamisetaDto(
            id = idPrueba, nombre = "Inter Miami", descripcion = "Messi", precio = 100.0,
            imagenUrl = "", stockS = 2, stockM = 5, stockL = 0, stockXL = 0, stock2XL = 0
        )
        val mockEntity = CamisetaEntity(
            id = idPrueba,
            nombre = "Inter Miami",
            descripcion = "Messi",
            precio = 100.0,
            imagenUrl = "",
            stockS = 2,
            stockM = 5,
            stockL = 0,
            stockXL = 0,
            stock2XL = 0,
            stockTotal = 7,
            categoriaId = 1
        )

        coEvery { remoteDataSource.getCamisetaById(idPrueba) } returns Result.success(mockDto)
        coEvery { localDataSource.upsert(any()) } just Runs
        every { localDataSource.observeById(idPrueba) } returns flowOf(mockEntity)

        repository.getCamisetaById(idPrueba).test {
            val loadingItem = awaitItem()
            assertTrue(loadingItem is Resource.Loading)

            val successItem = awaitItem()
            assertTrue(successItem is Resource.Success)
            assertEquals("Inter Miami", successItem.data?.nombre)

            awaitComplete()
        }
    }

    @Test
    fun `getCamisetaById usa la base de datos local si la API falla`() = runTest {
        val idPrueba = 1
        val mockEntity = CamisetaEntity(
            id = idPrueba,
            nombre = "Camiseta Offline",
            descripcion = "",
            precio = 50.0,
            imagenUrl = "",
            stockS = 1,
            stockM = 1,
            stockL = 1,
            stockXL = 1,
            stock2XL = 1,
            stockTotal = 5,
            categoriaId = 1
        )

        coEvery { remoteDataSource.getCamisetaById(idPrueba) } throws Exception("Sin internet")
        every { localDataSource.observeById(idPrueba) } returns flowOf(mockEntity)

        repository.getCamisetaById(idPrueba).test {
            assertTrue(awaitItem() is Resource.Loading)

            val successItem = awaitItem()
            assertTrue(successItem is Resource.Success)
            assertEquals("Camiseta Offline", successItem.data?.nombre)

            awaitComplete()
        }
    }
}