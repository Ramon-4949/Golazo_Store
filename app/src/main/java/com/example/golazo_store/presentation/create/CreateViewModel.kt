package com.example.golazo_store.presentation.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.model.Camiseta
import com.example.golazo_store.domain.repository.CamisetaRepository
import com.example.golazo_store.domain.repository.CategoriaRepository
import com.example.golazo_store.domain.repository.UploadRepository
import com.example.golazo_store.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val categoriaRepository: CategoriaRepository,
    private val camisetaRepository: CamisetaRepository,
    private val uploadRepository: UploadRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val currentCamisetaId: Int = savedStateHandle.get<Int>("id") ?: -1

    private val _state = MutableStateFlow(CreateUiState())
    val state: StateFlow<CreateUiState> = _state.asStateFlow()

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
                                    originalImageUrl = camiseta.imagenUrl
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

    fun onEvent(event: CreateEvent) {
        when (event) {
            is CreateEvent.OnNombreChange -> _state.update { it.copy(nombre = event.nombre) }
            is CreateEvent.OnDescripcionChange -> _state.update { it.copy(descripcion = event.descripcion) }
            is CreateEvent.OnPrecioChange -> _state.update { it.copy(precio = event.precio) }
            is CreateEvent.OnStockSChange -> _state.update { it.copy(stockS = event.stock) }
            is CreateEvent.OnStockMChange -> _state.update { it.copy(stockM = event.stock) }
            is CreateEvent.OnStockLChange -> _state.update { it.copy(stockL = event.stock) }
            is CreateEvent.OnStockXLChange -> _state.update { it.copy(stockXL = event.stock) }
            is CreateEvent.OnStock2XLChange -> _state.update { it.copy(stock2XL = event.stock) }
            is CreateEvent.OnCategoriaSelected -> _state.update { it.copy(selectedCategoriaId = event.categoriaId) }
            is CreateEvent.OnImagePicked -> _state.update { it.copy(imageUri = event.uri) }
            CreateEvent.SaveProduct -> saveProduct()
            CreateEvent.ClearMessages -> _state.update { it.copy(successMessage = null, errorMessage = null) }
        }
    }

    private fun saveProduct() {
        val currentState = _state.value

        // If editing, imageUri could be null initially if they don't pick a new one, 
        // but for now we enforce re-selecting or assume they need to. 
        if (currentState.nombre.isBlank() || currentState.precio.isBlank() || currentState.selectedCategoriaId == null) {
            _state.update { it.copy(errorMessage = "Por favor completa el nombre, precio, y categoría.") }
            return
        }

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
            } else if (finalImageUrl.isBlank()) {
                _state.update { it.copy(isLoading = false, errorMessage = "Debes seleccionar una imagen para la camiseta") }
                return@launch
            }

            val nuevaCamiseta = Camiseta(
                id = if (currentCamisetaId != -1) currentCamisetaId else 0,
                nombre = currentState.nombre,
                descripcion = currentState.descripcion,
                precio = currentState.precio.toDoubleOrNull() ?: 0.0,
                imagenUrl = finalImageUrl,
                stockS = currentState.stockS.toIntOrNull() ?: 0,
                stockM = currentState.stockM.toIntOrNull() ?: 0,
                stockL = currentState.stockL.toIntOrNull() ?: 0,
                stockXL = currentState.stockXL.toIntOrNull() ?: 0,
                stock2XL = currentState.stock2XL.toIntOrNull() ?: 0,
                categoriaId = currentState.selectedCategoriaId
            )

            val result = camisetaRepository.upsertCamiseta(nuevaCamiseta)

            when (result) {
                is Resource.Success -> {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            successMessage = if (currentCamisetaId != -1) "¡Camiseta actualizada!" else "¡Camiseta creada exitosamente!",
                            nombre = "", descripcion = "", precio = "",
                            stockS = "", stockM = "", stockL = "", stockXL = "", stock2XL = "",
                            selectedCategoriaId = null, imageUri = null, originalImageUrl = null
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }
}
