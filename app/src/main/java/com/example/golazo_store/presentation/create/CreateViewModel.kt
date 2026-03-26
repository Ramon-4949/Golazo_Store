package com.example.golazo_store.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreateViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(CreateUiState())
    val state: StateFlow<CreateUiState> = _state.asStateFlow()

    fun onEvent(event: CreateEvent) {
        when (event) {
            is CreateEvent.OnEquipoChange -> _state.update { it.copy(equipo = event.equipo) }
            is CreateEvent.OnLigaChange -> _state.update { it.copy(liga = event.liga) }
            is CreateEvent.OnTemporadaChange -> _state.update { it.copy(temporada = event.temporada) }
            is CreateEvent.OnDescripcionChange -> _state.update { it.copy(descripcion = event.descripcion) }
            is CreateEvent.OnPrecioChange -> _state.update { it.copy(precio = event.precio) }
            is CreateEvent.OnStockChange -> _state.update { it.copy(stock = event.stock) }
            is CreateEvent.OnImagePicked -> _state.update { it.copy(imageUri = event.uri) }
            CreateEvent.SaveProduct -> saveProduct()
            CreateEvent.ClearMessages -> _state.update { it.copy(successMessage = null, errorMessage = null) }
        }
    }

    private fun saveProduct() {
        val currentState = _state.value

        if (currentState.equipo.isBlank() || currentState.precio.isBlank() || currentState.imageUri == null) {
            _state.update { it.copy(errorMessage = "Por favor completa el equipo, el precio y selecciona una imagen.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            
            // Simulación de red
            delay(1500)

            _state.update { 
                it.copy(
                    isLoading = false,
                    successMessage = "¡Camiseta creada exitosamente!",
                    equipo = "",
                    liga = "",
                    temporada = "",
                    descripcion = "",
                    precio = "",
                    stock = "",
                    imageUri = null
                )
            }
        }
    }
}
