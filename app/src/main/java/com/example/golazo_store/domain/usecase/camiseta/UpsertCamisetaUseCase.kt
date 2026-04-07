package com.example.golazo_store.domain.usecase.camiseta

import com.example.golazo_store.domain.model.Camiseta
import com.example.golazo_store.domain.repository.CamisetaRepository
import com.example.golazo_store.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpsertCamisetaUseCase @Inject constructor(
    private val repository: CamisetaRepository
) {
    operator fun invoke(camiseta: Camiseta, originalPrecioStr: String): Flow<UpsertCamisetaResult> = flow {
        var isValid = true
        var nombreError: String? = null
        var descError: String? = null
        var precioError: String? = null
        var imageError: String? = null
        var catError: String? = null
        var sError: String? = null
        var mError: String? = null
        var lError: String? = null
        var xlError: String? = null
        var xxlError: String? = null

        if (camiseta.nombre.isBlank()) {
            nombreError = "El nombre no puede estar vacío"
            isValid = false
        }
        if (camiseta.descripcion.isBlank()) {
            descError = "La descripción no puede estar vacía"
            isValid = false
        }
        
        val precioNum = originalPrecioStr.toDoubleOrNull()
        if (precioNum == null || precioNum <= 0) {
            precioError = "El precio debe ser mayor a 0"
            isValid = false
        }
        if (camiseta.imagenUrl.isBlank()) {
            imageError = "Debe seleccionar una imagen"
            isValid = false
        }
        if (camiseta.categoriaId == null || camiseta.categoriaId == 0) {
            catError = "Debe seleccionar una categoría válida"
            isValid = false
        }
        if (camiseta.stockS < 0) {
            sError = "No puede ser negativo"
            isValid = false
        }
        if (camiseta.stockM < 0) {
            mError = "No puede ser negativo"
            isValid = false
        }
        if (camiseta.stockL < 0) {
            lError = "No puede ser negativo"
            isValid = false
        }
        if (camiseta.stockXL < 0) {
            xlError = "No puede ser negativo"
            isValid = false
        }
        if (camiseta.stock2XL < 0) {
            xxlError = "No puede ser negativo"
            isValid = false
        }

        if (!isValid) {
            emit(UpsertCamisetaResult.ValidationError(
                nombreError = nombreError,
                descripcionError = descError,
                precioError = precioError,
                categoriasError = catError,
                imagenError = imageError,
                stockSError = sError,
                stockMError = mError,
                stockLError = lError,
                stockXLError = xlError,
                stock2XLError = xxlError
            ))
            return@flow
        }

        emit(UpsertCamisetaResult.Loading)
        val result = repository.upsertCamiseta(camiseta)
        when (result) {
            is Resource.Success -> emit(UpsertCamisetaResult.Success("Guardado exitosamente"))
            is Resource.Error -> emit(UpsertCamisetaResult.Error(result.message ?: "Ocurrió un error"))
            else -> {}
        }
    }
}

sealed class UpsertCamisetaResult {
    data class Success(val message: String) : UpsertCamisetaResult()
    data class Error(val message: String) : UpsertCamisetaResult()
    object Loading : UpsertCamisetaResult()
    data class ValidationError(
        val nombreError: String? = null,
        val descripcionError: String? = null,
        val precioError: String? = null,
        val categoriasError: String? = null,
        val imagenError: String? = null,
        val stockSError: String? = null,
        val stockMError: String? = null,
        val stockLError: String? = null,
        val stockXLError: String? = null,
        val stock2XLError: String? = null
    ) : UpsertCamisetaResult()
}