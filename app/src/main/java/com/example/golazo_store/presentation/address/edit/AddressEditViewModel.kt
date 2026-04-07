package com.example.golazo_store.presentation.address.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.data.local.SessionManager
import com.example.golazo_store.domain.usecase.address.GetDireccionByIdUseCase
import com.example.golazo_store.domain.usecase.address.SaveAddressResult
import com.example.golazo_store.domain.usecase.address.SaveAddressUseCase
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
    private val getDireccionByIdUseCase: GetDireccionByIdUseCase,
    private val saveAddressUseCase: SaveAddressUseCase,
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
            getDireccionByIdUseCase(id).collect { resource ->
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
            is AddressEditEvent.OnNombreDireccionChange -> _state.update { it.copy(nombreDireccion = event.value, nombreError = null) }
            is AddressEditEvent.OnCalleNumeroChange -> _state.update { it.copy(calleNumero = event.value, calleError = null) }
            is AddressEditEvent.OnProvinciaChange -> _state.update { it.copy(provincia = event.value, provinciaError = null) }
            is AddressEditEvent.OnCodigoPostalChange -> _state.update { it.copy(codigoPostal = event.value, codigoPostalError = null) }
            is AddressEditEvent.OnCiudadChange -> _state.update { it.copy(ciudad = event.value, ciudadError = null) }
            is AddressEditEvent.OnReferenceChange -> _state.update { it.copy(reference = event.value) }
            is AddressEditEvent.OnEsPrincipalChange -> _state.update { it.copy(esPrincipal = event.value) }
            is AddressEditEvent.OnSave -> saveAddress()
        }
    }

    private fun saveAddress() {
        val currentState = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val userId = sessionManager.getUserSession()?.id ?: 0

            val result = saveAddressUseCase(
                id = currentState.id?.takeIf { it != -1 },
                usuarioId = userId,
                nombreDireccion = currentState.nombreDireccion,
                calleNumero = currentState.calleNumero,
                provincia = currentState.provincia,
                codigoPostal = currentState.codigoPostal,
                ciudad = currentState.ciudad,
                reference = currentState.reference,
                esPrincipal = currentState.esPrincipal
            )

            when (result) {
                is SaveAddressResult.Success -> {
                    _state.update { it.copy(isLoading = false, isSaved = true) }
                }
                is SaveAddressResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is SaveAddressResult.ValidationError -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            nombreError = result.nombreError,
                            calleError = result.calleError,
                            provinciaError = result.provinciaError,
                            codigoPostalError = result.codigoPostalError,
                            ciudadError = result.ciudadError
                        )
                    }
                }
            }
        }
    }
}
