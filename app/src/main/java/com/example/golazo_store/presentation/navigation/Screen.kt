package com.example.golazo_store.presentation.navigation

import kotlinx.serialization.Serializable


sealed class Screen {
    @Serializable
    data object Login : Screen()

    @Serializable
    data object Register : Screen()

    @Serializable
    data object RegistroList : Screen()

    @Serializable
    data class RegistroDetail(val id: Int) : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data class CreateCamiseta(val id: Int = -1) : Screen()

    @Serializable
    data object GestionPublicaciones : Screen()

    @Serializable
    data object Categories : Screen()

    @Serializable
    data object Favorites : Screen()

    @Serializable
    data object Profile : Screen()
}