package com.example.golazo_store.presentation.profile

import androidx.lifecycle.ViewModel
import com.example.golazo_store.data.local.SessionManager
import com.example.golazo_store.domain.model.Usuario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

import androidx.lifecycle.viewModelScope
import com.example.golazo_store.domain.repository.FavoritesRepository
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<Usuario?>(null)
    val userState = _userState.asStateFlow()

    init {
        loadUser()
    }

    fun loadUser() {
        _userState.value = sessionManager.getUserSession()
    }

    fun logout() {
        viewModelScope.launch {
            favoritesRepository.clearAllFavorites()
            sessionManager.clearSession()
            _userState.value = null
        }
    }
}

