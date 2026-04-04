package com.example.golazo_store.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.usecase.Login.LoginUseCase
import com.example.golazo_store.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.CorreoChanged -> {
                _state.update { it.copy(correo = event.correo, error = null) }
            }
            is LoginEvent.ContrasenaChanged -> {
                _state.update { it.copy(contrasena = event.contrasena, error = null) }
            }
            LoginEvent.LoginClicked -> login()
        }
    }

    private fun login() {
        val current = _state.value
        if (current.correo.isBlank() || current.contrasena.isBlank()) {
            _state.update { it.copy(error = "Por favor, complete todos los campos") }
            return
        }

        viewModelScope.launch {
            loginUseCase(correo = current.correo.trim(), password = current.contrasena.trim()).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false, isSuccess = true) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }
}

