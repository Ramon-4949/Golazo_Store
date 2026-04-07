package com.example.golazo_store.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.usecase.Login.RegisterResult
import com.example.golazo_store.domain.usecase.Login.RegisterUseCase
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
                _state.update { it.copy(nombre = event.nombre, nombreError = null) }
            }
            is RegisterEvent.CorreoChanged -> {
                _state.update { it.copy(correo = event.correo, emailError = null) }
            }
            is RegisterEvent.ContrasenaChanged -> {
                _state.update { it.copy(contrasena = event.contrasena, passwordError = null) }
            }
            is RegisterEvent.ConfirmContrasenaChanged -> {
                _state.update { it.copy(confirmContrasena = event.confirmContrasena, confirmPasswordError = null) }
            }
            RegisterEvent.RegisterClicked -> register()
        }
    }

    private fun register() {
        val current = _state.value

        if (current.contrasena != current.confirmContrasena) {
            _state.update { it.copy(confirmPasswordError = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            registerUseCase(
                nombre = current.nombre.trim(), 
                correo = current.correo.trim(), 
                password = current.contrasena.trim()
            ).collect { result ->
                when (result) {
                    is RegisterResult.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is RegisterResult.Success -> {
                        _state.update { it.copy(isLoading = false, isSuccess = true) }
                    }
                    is RegisterResult.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is RegisterResult.ValidationError -> {
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                nombreError = result.nombreError,
                                emailError = result.emailError,
                                passwordError = result.passwordError
                            ) 
                        }
                    }
                }
            }
        }
    }
}

