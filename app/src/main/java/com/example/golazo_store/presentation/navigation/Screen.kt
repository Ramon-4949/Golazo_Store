package com.example.golazo_store.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object RegistroList : Screen()

    @Serializable
    data class RegistroDetail(val id: Int) : Screen()
}