package com.example.golazo_store.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.model.Usuario
import com.example.golazo_store.domain.usecase.profile.GetUserSessionUseCase
import com.example.golazo_store.domain.usecase.profile.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserSessionUseCase: GetUserSessionUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _userState = MutableStateFlow<Usuario?>(null)
    val userState = _userState.asStateFlow()

    init {
        loadUser()
    }

    fun loadUser() {
        _userState.value = getUserSessionUseCase()
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _userState.value = null
        }
    }
}

