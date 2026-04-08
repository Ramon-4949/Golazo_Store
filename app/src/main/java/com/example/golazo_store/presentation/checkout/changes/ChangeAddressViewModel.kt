package com.example.golazo_store.presentation.checkout.changes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class ChangeAddressState(
    val direcciones: List<Direccion> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ChangeAddressViewModel @Inject constructor(
    private val repository: DireccionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChangeAddressState())
    val state: StateFlow<ChangeAddressState> = _state.asStateFlow()

    init {
        loadDirecciones()
    }

    fun loadDirecciones() {
        viewModelScope.launch {
            repository.getDirecciones().collect { result ->
                if (result is Resource.Success) {
                    _state.update { 
                        it.copy(
                            direcciones = result.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}
