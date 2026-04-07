package com.example.golazo_store.domain.usecase.profile

import com.example.golazo_store.data.local.SessionManager
import com.example.golazo_store.domain.repository.FavoritesRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke() {
        favoritesRepository.clearAllFavorites()
        sessionManager.clearSession()
    }
}
