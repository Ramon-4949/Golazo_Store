package com.example.golazo_store.presentation.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.data.local.SessionManager
import com.example.golazo_store.domain.repository.AuthRepository
import com.example.golazo_store.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state: StateFlow<EditProfileState> = _state.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        val user = sessionManager.getUserSession()
        if (user != null) {
            _state.update {
                it.copy(
                    id = user.id,
                    nombreUsuario = user.nombre
                )
            }
        }
    }

    fun onEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.OnNombreUsuarioChange -> {
                _state.update { it.copy(nombreUsuario = event.nombre) }
            }
            is EditProfileEvent.OnNuevaContrasenaChange -> {
                _state.update { it.copy(nuevaContrasena = event.contrasena) }
            }
            is EditProfileEvent.OnConfirmarContrasenaChange -> {
                _state.update { it.copy(confirmarContrasena = event.contrasena) }
            }
            is EditProfileEvent.OnGuardarCambios -> {
                guardarCambios()
            }
            is EditProfileEvent.OnEliminarCuenta -> {
                eliminarCuenta()
            }
            is EditProfileEvent.OnDismissError -> {
                _state.update { it.copy(error = null) }
            }
            is EditProfileEvent.OnDismissSuccess -> {
                _state.update { it.copy(successMessage = null) }
            }
        }
    }

    private fun guardarCambios() {
        val currentState = _state.value

        if (currentState.nombreUsuario.isBlank()) {
            _state.update { it.copy(error = "El nombre de usuario no puede estar vacío.") }
            return
        }

        if (currentState.nuevaContrasena.isNotBlank() || currentState.confirmarContrasena.isNotBlank()) {
            if (currentState.nuevaContrasena != currentState.confirmarContrasena) {
                _state.update { it.copy(error = "Las contraseñas no coinciden.") }
                return
            }
        }

        viewModelScope.launch {
            val contrasena = if (currentState.nuevaContrasena.isNotBlank()) currentState.nuevaContrasena else null
            authRepository.updateProfile(
                id = currentState.id,
                nombreUsuario = currentState.nombreUsuario,
                nuevaContrasena = contrasena
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                successMessage = result.data ?: "Perfil actualizado exitosamente",
                                nuevaContrasena = "",
                                confirmarContrasena = ""
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "Ocurrió un error al actualizar el perfil"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun eliminarCuenta() {
        val currentState = _state.value
        viewModelScope.launch {
            authRepository.deleteAccount(currentState.id).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                successMessage = "Cuenta_Eliminada" // Indicador especial
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "Ocurrió un error al eliminar la cuenta"
                            )
                        }
                    }
                }
            }
        }
    }
}
