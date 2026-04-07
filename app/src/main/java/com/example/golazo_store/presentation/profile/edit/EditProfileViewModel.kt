package com.example.golazo_store.presentation.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.usecase.profile.DeleteAccountUseCase
import com.example.golazo_store.domain.usecase.profile.GetUserSessionUseCase
import com.example.golazo_store.domain.usecase.profile.UpdateProfileResult
import com.example.golazo_store.domain.usecase.profile.UpdateProfileUseCase
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
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getUserSessionUseCase: GetUserSessionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state: StateFlow<EditProfileState> = _state.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        val user = getUserSessionUseCase()
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
                _state.update { it.copy(nombreUsuario = event.nombre, nombreError = null) }
            }
            is EditProfileEvent.OnNuevaContrasenaChange -> {
                _state.update { it.copy(nuevaContrasena = event.contrasena, passwordError = null) }
            }
            is EditProfileEvent.OnConfirmarContrasenaChange -> {
                _state.update { it.copy(confirmarContrasena = event.contrasena, confirmPasswordError = null) }
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

        viewModelScope.launch {
            updateProfileUseCase(
                id = currentState.id,
                nombreUsuario = currentState.nombreUsuario,
                nuevaContrasena = currentState.nuevaContrasena,
                confirmarContrasena = currentState.confirmarContrasena
            ).collect { result ->
                when (result) {
                    is UpdateProfileResult.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is UpdateProfileResult.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                successMessage = result.message,
                                nuevaContrasena = "",
                                confirmarContrasena = ""
                            )
                        }
                    }
                    is UpdateProfileResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                    is UpdateProfileResult.ValidationError -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                nombreError = result.nombreError,
                                passwordError = result.passwordError,
                                confirmPasswordError = result.confirmPasswordError
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
            deleteAccountUseCase(currentState.id).collect { result ->
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
