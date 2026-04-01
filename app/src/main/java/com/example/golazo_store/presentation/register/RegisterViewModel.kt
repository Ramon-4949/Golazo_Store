package com.example.golazo_store.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.usecase.Login.RegisterUseCase
import com.example.golazo_store.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.NombreChanged -> {
                _state.update { it.copy(nombre = event.nombre, error = null) }
            }
            is RegisterEvent.CorreoChanged -> {
                _state.update { it.copy(correo = event.correo, error = null) }
            }
            is RegisterEvent.ContrasenaChanged -> {
                _state.update { it.copy(contrasena = event.contrasena, error = null) }
            }
            is RegisterEvent.ConfirmContrasenaChanged -> {
                _state.update { it.copy(confirmContrasena = event.confirmContrasena, error = null) }
            }
            RegisterEvent.RegisterClicked -> register()
        }
    }

    private fun register() {
        val current = _state.value

        if (current.nombre.isBlank() || current.correo.isBlank() || 
            current.contrasena.isBlank() || current.confirmContrasena.isBlank()) {
            _state.update { it.copy(error = "Por favor, complete todos los campos") }
            return
        }

        if (current.contrasena != current.confirmContrasena) {
            _state.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            registerUseCase(
                nombre = current.nombre.trim(), 
                correo = current.correo.trim(), 
                password = current.contrasena.trim()
            ).collect { result ->
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
