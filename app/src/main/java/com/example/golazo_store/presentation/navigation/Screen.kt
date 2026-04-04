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
    data class CamisetaDetail(val id: Int) : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data class CreateCamiseta(val id: Int = -1) : Screen()

    @Serializable
    data object GestionPublicaciones : Screen()

    @Serializable
    data object AdminPedidos : Screen()

    @Serializable
    data class AdminPedidoDetail(val pedidoId: Int) : Screen()

    @Serializable
    data object Categories : Screen()

    @Serializable
    data object Favorites : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data object Cart : Screen()

    @Serializable
    data object AddressList : Screen()

    @Serializable
    data class AddressEdit(val id: Int? = null) : Screen()

    @Serializable
    data object PaymentList : Screen()

    @Serializable
    data object PaymentAdd : Screen()

    @Serializable
    data object Checkout : Screen()

    @Serializable
    data class CheckoutSuccess(val orderNumber: String) : Screen()

    @Serializable
    data object MisPedidos : Screen()

    @Serializable
    data class RastrearPedido(val pedidoId: Int) : Screen()
}
