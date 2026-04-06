package com.example.golazo_store.presentation.checkout.changes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.model.MetodoPago
import com.example.golazo_store.domain.repository.MetodoPagoRepository
import com.example.golazo_store.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChangePaymentState(
    val metodosPago: List<MetodoPago> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ChangePaymentViewModel @Inject constructor(
    private val repository: MetodoPagoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChangePaymentState())
    val state: StateFlow<ChangePaymentState> = _state.asStateFlow()

    init {
        loadMetodosPago()
    }

    private fun loadMetodosPago() {
        viewModelScope.launch {
            repository.getMetodosPago().collect { result ->
                if (result is Resource.Success) {
                    _state.update { 
                        it.copy(
                            metodosPago = result.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}
