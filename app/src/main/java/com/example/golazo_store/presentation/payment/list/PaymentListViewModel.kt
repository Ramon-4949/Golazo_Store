package com.example.golazo_store.presentation.payment.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.usecase.payment.GetMetodosPagoUseCase
import com.example.golazo_store.domain.usecase.payment.DeleteMetodoPagoUseCase
import com.example.golazo_store.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentListViewModel @Inject constructor(
    private val getMetodosPagoUseCase: GetMetodosPagoUseCase,
    private val deleteMetodoPagoUseCase: DeleteMetodoPagoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentListUiState(isLoading = true))
    val state: StateFlow<PaymentListUiState> = _state.asStateFlow()

    init {
        loadPayments()
    }

    private fun loadPayments() {
        viewModelScope.launch {
            getMetodosPagoUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                payments = resource.data ?: emptyList(),
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(isLoading = false, errorMessage = resource.message ?: "Error desconocido al cargar tarjetas")
                        }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun onEvent(event: PaymentListEvent) {
        when (event) {
            is PaymentListEvent.LoadPayments -> {
                loadPayments()
            }
            is PaymentListEvent.OnDeletePayment -> {
                viewModelScope.launch {
                    val result = deleteMetodoPagoUseCase(event.id)
                    if (result is Resource.Success) {
                        loadPayments()
                    } else if (result is Resource.Error) {
                        _state.update { it.copy(errorMessage = result.message) }
                    }
                }
            }
        }
    }
}
