package com.example.golazo_store.presentation.address.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.usecase.address.GetDireccionesUseCase
import com.example.golazo_store.domain.usecase.address.DeleteDireccionUseCase
import com.example.golazo_store.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressListViewModel @Inject constructor(
    private val getDireccionesUseCase: GetDireccionesUseCase,
    private val deleteDireccionUseCase: DeleteDireccionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddressListUiState(isLoading = true))
    val state: StateFlow<AddressListUiState> = _state.asStateFlow()

    private var allAddresses = listOf<com.example.golazo_store.domain.model.Direccion>()

    init {
        loadAddresses()
    }

    private fun loadAddresses() {
        viewModelScope.launch {
            getDireccionesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        allAddresses = resource.data ?: emptyList()
                        _state.update {
                            it.copy(
                                isLoading = false,
                                addresses = filterAddresses(allAddresses, it.searchQuery),
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(isLoading = false, errorMessage = resource.message ?: "Error desconocido")
                        }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun onEvent(event: AddressListEvent) {
        when (event) {
            is AddressListEvent.LoadAddresses -> {
                loadAddresses()
            }
            is AddressListEvent.OnSearchQueryChange -> {
                _state.update {
                    it.copy(
                        searchQuery = event.query,
                        addresses = filterAddresses(allAddresses, event.query)
                    )
                }
            }
            is AddressListEvent.OnDeleteAddress -> {
                viewModelScope.launch {
                    val result = deleteDireccionUseCase(event.id)
                    if (result is Resource.Success) {
                        loadAddresses() // Reload after deletion
                    } else if (result is Resource.Error) {
                        _state.update { it.copy(errorMessage = result.message) }
                    }
                }
            }
        }
    }

    private fun filterAddresses(addresses: List<com.example.golazo_store.domain.model.Direccion>, query: String): List<com.example.golazo_store.domain.model.Direccion> {
        if (query.isBlank()) return addresses
        return addresses.filter {
            it.nombreDireccion.contains(query, ignoreCase = true) ||
            it.calleNumero.contains(query, ignoreCase = true) ||
            it.ciudad.contains(query, ignoreCase = true)
        }
    }
}
