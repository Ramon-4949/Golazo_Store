package com.example.golazo_store.presentation.CreateEdit.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.model.Camiseta
import com.example.golazo_store.domain.repository.CamisetaRepository
import com.example.golazo_store.domain.repository.CategoriaRepository
import com.example.golazo_store.domain.repository.UploadRepository
import com.example.golazo_store.domain.usecase.camiseta.UpsertCamisetaResult
import com.example.golazo_store.domain.usecase.camiseta.UpsertCamisetaUseCase
import com.example.golazo_store.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateEditViewModel @Inject constructor(
    private val categoriaRepository: CategoriaRepository,
    private val camisetaRepository: CamisetaRepository,
    private val uploadRepository: UploadRepository,
    private val upsertCamisetaUseCase: UpsertCamisetaUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val currentCamisetaId: Int = savedStateHandle.get<Int>("id") ?: -1

    private val _state = MutableStateFlow(CreateEditUiState())
    val state: StateFlow<CreateEditUiState> = _state.asStateFlow()

    init {
        loadCategorias()
        if (currentCamisetaId != -1) {
            _state.update { it.copy(isEditing = true) }
            loadCamisetaParaEditar(currentCamisetaId)
        }
    }

    private fun loadCamisetaParaEditar(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            camisetaRepository.getCamisetaById(id).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { camiseta ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    nombre = camiseta.nombre,
                                    descripcion = camiseta.descripcion,
                                    precio = camiseta.precio.toString(),
                                    stockS = camiseta.stockS.toString(),
                                    stockM = camiseta.stockM.toString(),
                                    stockL = camiseta.stockL.toString(),
                                    stockXL = camiseta.stockXL.toString(),
                                    stock2XL = camiseta.stock2XL.toString(),
                                    selectedCategoriaId = camiseta.categoriaId,
                                    originalImageUrl = camiseta.imagenUrl,
                                    errorMessage = null
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, errorMessage = "Error al cargar datos para edición.") }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    private fun loadCategorias() {
        viewModelScope.launch {
            categoriaRepository.getCategorias().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _state.update { it.copy(categorias = resource.data ?: emptyList()) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(errorMessage = resource.message) }
                    }
                    is Resource.Loading -> { } // Maybe show separate loading if needed
                }
            }
        }
    }

    fun onEvent(event: CreateEditEvent) {
        when (event) {
            is CreateEditEvent.OnNombreChange -> _state.update { it.copy(nombre = event.nombre, nombreError = null) }
            is CreateEditEvent.OnDescripcionChange -> _state.update { it.copy(descripcion = event.descripcion, descripcionError = null) }
            is CreateEditEvent.OnPrecioChange -> _state.update { it.copy(precio = event.precio, precioError = null) }
            is CreateEditEvent.OnStockSChange -> _state.update { it.copy(stockS = event.stock, stockSError = null) }
            is CreateEditEvent.OnStockMChange -> _state.update { it.copy(stockM = event.stock, stockMError = null) }
            is CreateEditEvent.OnStockLChange -> _state.update { it.copy(stockL = event.stock, stockLError = null) }
            is CreateEditEvent.OnStockXLChange -> _state.update { it.copy(stockXL = event.stock, stockXLError = null) }
            is CreateEditEvent.OnStock2XLChange -> _state.update { it.copy(stock2XL = event.stock, stock2XLError = null) }
            is CreateEditEvent.OnCategoriaSelected -> _state.update { it.copy(selectedCategoriaId = event.categoriaId, categoriaError = null) }
            is CreateEditEvent.OnImagePicked -> _state.update { it.copy(imageUri = event.uri, imageError = null) }
            CreateEditEvent.SaveProduct -> saveProduct()
            CreateEditEvent.ClearMessages -> _state.update { it.copy(successMessage = null, errorMessage = null) }
        }
    }

    private fun saveProduct() {
        val currentState = _state.value

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            
            var finalImageUrl = currentState.originalImageUrl ?: ""

            if (currentState.imageUri != null) {
                val uploadResult = uploadRepository.uploadImage(currentState.imageUri)
                if (uploadResult is Resource.Success && uploadResult.data != null) {
                    finalImageUrl = uploadResult.data
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = uploadResult.message ?: "Error al subir la imagen") }
                    return@launch
                }
            }

            val nuevaCamiseta = Camiseta(
                id = if (currentCamisetaId != -1) currentCamisetaId else 0,
                nombre = currentState.nombre,
                descripcion = currentState.descripcion,
                precio = currentState.precio.toDoubleOrNull() ?: 0.0,
                imagenUrl = finalImageUrl,
                stockS = currentState.stockS.toIntOrNull() ?: if (currentState.stockS.isBlank()) 0 else -1,
                stockM = currentState.stockM.toIntOrNull() ?: if (currentState.stockM.isBlank()) 0 else -1,
                stockL = currentState.stockL.toIntOrNull() ?: if (currentState.stockL.isBlank()) 0 else -1,
                stockXL = currentState.stockXL.toIntOrNull() ?: if (currentState.stockXL.isBlank()) 0 else -1,
                stock2XL = currentState.stock2XL.toIntOrNull() ?: if (currentState.stock2XL.isBlank()) 0 else -1,
                categoriaId = currentState.selectedCategoriaId ?: 0
            )

            upsertCamisetaUseCase(nuevaCamiseta, currentState.precio).collect { result ->
                when (result) {
                    is UpsertCamisetaResult.Success -> {
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                successMessage = if (currentCamisetaId != -1) "¡Camiseta actualizada!" else "¡Camiseta creada exitosamente!",
                                nombre = "", descripcion = "", precio = "",
                                stockS = "", stockM = "", stockL = "", stockXL = "", stock2XL = "",
                                selectedCategoriaId = null, imageUri = null, originalImageUrl = null,
                                nombreError = null, descripcionError = null, precioError = null,
                                categoriaError = null, imageError = null, stockSError = null,
                                stockMError = null, stockLError = null, stockXLError = null,
                                stock2XLError = null
                            )
                        }
                    }
                    is UpsertCamisetaResult.Error -> {
                        _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                    }
                    is UpsertCamisetaResult.Loading -> {
                        _state.update { 
                            it.copy(
                                isLoading = true, errorMessage = null,
                                nombreError = null, descripcionError = null, precioError = null,
                                categoriaError = null, imageError = null, stockSError = null,
                                stockMError = null, stockLError = null, stockXLError = null, stock2XLError = null
                            ) 
                        }
                    }
                    is UpsertCamisetaResult.ValidationError -> {
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                nombreError = result.nombreError,
                                descripcionError = result.descripcionError,
                                precioError = result.precioError,
                                categoriaError = result.categoriasError,
                                imageError = result.imagenError,
                                stockSError = result.stockSError,
                                stockMError = result.stockMError,
                                stockLError = result.stockLError,
                                stockXLError = result.stockXLError,
                                stock2XLError = result.stock2XLError
                            )
                        }
                    }
                }
            }
        }
    }
}


