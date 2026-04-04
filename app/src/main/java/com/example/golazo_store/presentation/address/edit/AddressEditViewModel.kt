package com.example.golazo_store.presentation.address.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.data.local.SessionManager
import com.example.golazo_store.domain.model.Direccion
import com.example.golazo_store.domain.repository.DireccionRepository
import com.example.golazo_store.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressEditViewModel @Inject constructor(
    private val repository: DireccionRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddressEditUiState())
    val state: StateFlow<AddressEditUiState> = _state.asStateFlow()

    init {
        savedStateHandle.get<Int>("id")?.let { id ->
            _state.update { it.copy(id = id) }
            if (id != -1) {
                loadAddress(id)
            }
        }
    }

    private fun loadAddress(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getDireccionById(id).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { dir ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    nombreDireccion = dir.nombreDireccion,
                                    calleNumero = dir.calleNumero,
                                    provincia = dir.provincia,
                                    codigoPostal = dir.codigoPostal,
                                    ciudad = dir.ciudad,
                                    reference = dir.reference,
                                    esPrincipal = dir.esPrincipal
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(isLoading = false, error = resource.message ?: "Error al cargar dirección")
                        }
                    }
                    is Resource.Loading -> { }
                }
            }
        }
    }

    fun onEvent(event: AddressEditEvent) {
        when (event) {
            is AddressEditEvent.OnNombreDireccionChange -> _state.update { it.copy(nombreDireccion = event.value) }
            is AddressEditEvent.OnCalleNumeroChange -> _state.update { it.copy(calleNumero = event.value) }
            is AddressEditEvent.OnProvinciaChange -> _state.update { it.copy(provincia = event.value) }
            is AddressEditEvent.OnCodigoPostalChange -> _state.update { it.copy(codigoPostal = event.value) }
            is AddressEditEvent.OnCiudadChange -> _state.update { it.copy(ciudad = event.value) }
            is AddressEditEvent.OnReferenceChange -> _state.update { it.copy(reference = event.value) }
            is AddressEditEvent.OnEsPrincipalChange -> _state.update { it.copy(esPrincipal = event.value) }
            is AddressEditEvent.OnSave -> saveAddress()
        }
    }

    private fun saveAddress() {
        val currentState = _state.value
        if (currentState.nombreDireccion.isBlank() || currentState.calleNumero.isBlank() ||
            currentState.provincia.isBlank() || currentState.codigoPostal.isBlank() || currentState.ciudad.isBlank()
        ) {
            _state.update { it.copy(error = "Por favor completa los campos obligatorios.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val userId = sessionManager.getUserSession()?.id ?: 0

            val direccion = Direccion(
                id = currentState.id ?: 0,
                usuarioId = userId,
                nombreDireccion = currentState.nombreDireccion,
                calleNumero = currentState.calleNumero,
                provincia = currentState.provincia,
                codigoPostal = currentState.codigoPostal,
                ciudad = currentState.ciudad,
                reference = currentState.reference,
                esPrincipal = currentState.esPrincipal
            )

            val result = if (currentState.id != null && currentState.id != -1) {
                repository.updateDireccion(currentState.id, direccion)
            } else {
                repository.createDireccion(direccion)
            }

            if (result is Resource.Success) {
                _state.update { it.copy(isLoading = false, isSaved = true) }
            } else if (result is Resource.Error) {
                _state.update { it.copy(isLoading = false, error = result.message ?: "Error al guardar dirección") }
            }
        }
    }
}
