package com.example.golazo_store.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.usecase.Login.LoginResult
import com.example.golazo_store.domain.usecase.Login.LoginUseCase
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
                _state.update { it.copy(correo = event.correo, emailError = null) }
            }
            is LoginEvent.ContrasenaChanged -> {
                _state.update { it.copy(contrasena = event.contrasena, passwordError = null) }
            }
            LoginEvent.LoginClicked -> login()
        }
    }

    private fun login() {
        val current = _state.value
        
        viewModelScope.launch {
            loginUseCase(correo = current.correo.trim(), password = current.contrasena.trim()).collect { result ->
                when (result) {
                    is LoginResult.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is LoginResult.Success -> {
                        _state.update { it.copy(isLoading = false, isSuccess = true) }
                    }
                    is LoginResult.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is LoginResult.ValidationError -> {
                        _state.update { 
                            it.copy(
                                isLoading = false,
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

