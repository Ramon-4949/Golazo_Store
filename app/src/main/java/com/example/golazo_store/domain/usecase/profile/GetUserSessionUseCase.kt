package com.example.golazo_store.domain.usecase.profile

import com.example.golazo_store.data.local.SessionManager
import com.example.golazo_store.domain.model.Usuario
import javax.inject.Inject

class GetUserSessionUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    operator fun invoke(): Usuario? {
        return sessionManager.getUserSession()
    }
}
