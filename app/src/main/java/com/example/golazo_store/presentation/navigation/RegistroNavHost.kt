package com.example.golazo_store.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.golazo_store.presentation.CreateEdit.edit.CreateEditScreen
import com.example.golazo_store.presentation.list.RegistroListScreen
import com.example.golazo_store.presentation.detail.CamisetaDetailScreen
import com.example.golazo_store.presentation.checkout.checkout_pages.CheckoutScreen
import com.example.golazo_store.presentation.checkout.checkout_pages.CheckoutSuccessScreen
import com.example.golazo_store.presentation.checkout.changes.ChangeAddressScreen
import com.example.golazo_store.presentation.checkout.changes.ChangePaymentScreen
import com.example.golazo_store.presentation.checkout.checkout_pages.CheckoutSuccessUiState
import com.example.golazo_store.presentation.checkout.checkout_pages.CheckoutSuccessViewModel
import com.example.golazo_store.presentation.checkout.checkout_pages.CheckoutViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.LaunchedEffect
import com.example.golazo_store.presentation.home.HomeScreen
import com.example.golazo_store.presentation.categories.CategoriesScreen
import com.example.golazo_store.presentation.favorites.FavoritesScreen
import com.example.golazo_store.presentation.components.GolazoBottomNavigation
import com.example.golazo_store.presentation.login.LoginScreen
import com.example.golazo_store.presentation.register.RegisterScreen
import com.example.golazo_store.presentation.profile.ProfileScreen
import com.example.golazo_store.presentation.create.list.CreateListScreen
import com.example.golazo_store.presentation.cart.CartScreen
import com.example.golazo_store.presentation.address.list.AddressListScreen
import com.example.golazo_store.presentation.address.edit.AddressEditScreen
import com.example.golazo_store.presentation.payment.list.PaymentListScreen
import com.example.golazo_store.presentation.payment.add.PaymentAddScreen
import com.example.golazo_store.presentation.admin_pedidos.AdminPedidosScreen
import com.example.golazo_store.presentation.admin_pedido_detail.AdminPedidoDetailScreen
import com.example.golazo_store.presentation.mis_pedidos.MisPedidosScreen
import com.example.golazo_store.presentation.rastrear_pedido.RastrearPedidoScreen


import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.golazo_store.data.local.SessionManager

@Composable
fun RegistroNavHost(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isLoggedIn = remember { sessionManager.getUserSession() != null }
    val startDestination = if (isLoggedIn) Screen.Home else Screen.Login

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    val bottomNavigation: @Composable () -> Unit = {
        GolazoBottomNavigation(
            currentRoute = currentRoute,
            onNavigateToHome = {
                navController.navigate(Screen.Home) {
                    popUpTo(Screen.Home) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onNavigateToCategories = {
                navController.navigate(Screen.Categories) {
                    popUpTo(Screen.Home) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onNavigateToFavorites = {
                navController.navigate(Screen.Favorites) {
                    popUpTo(Screen.Home) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onNavigateToProfile = {
                navController.navigate(Screen.Profile) {
                    popUpTo(Screen.Home) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Screen.Login> {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register) },
                onLoginSuccess = { 
                    navController.navigate(Screen.Home) { 
                        popUpTo(Screen.Login) { inclusive = true } 
                    } 
                }
            )
        }

        composable<Screen.Register> {
            RegisterScreen(
                onNavigateBack = { navController.navigateUp() },
                onRegisterSuccess = { navController.navigateUp() }
            )
        }

        composable<Screen.RegistroList> {
            RegistroListScreen(
                onNavigateToDetail = { id -> navController.navigate(Screen.CamisetaDetail(id)) },
                onNavigateToCreate = { navController.navigate(Screen.CreateCamiseta(-1)) }
            )
        }

        composable<Screen.CamisetaDetail> {
            CamisetaDetailScreen(
                onBack = { navController.navigateUp() },
                onAddToCartSuccess = { navController.navigate(Screen.Cart) }
            )
        }

        composable<Screen.Home> {
            HomeScreen(
                onNavigateToDetail = { id -> navController.navigate(Screen.CamisetaDetail(id)) },
                onNavigateToCreate = { navController.navigate(Screen.CreateCamiseta()) },
                onNavigateToCart = { navController.navigate(Screen.Cart) },
                bottomNavigation = bottomNavigation
            )
        }

        composable<Screen.Categories> {
            CategoriesScreen(
                bottomNavigation = bottomNavigation
            )
        }

        composable<Screen.Favorites> {
            FavoritesScreen(
                onBack = { navController.navigateUp() },
                onNavigateToDetail = { id -> navController.navigate(Screen.CamisetaDetail(id)) },
                onNavigateToCart = { navController.navigate(Screen.Cart) },
                bottomNavigation = bottomNavigation
            )
        }

        composable<Screen.CreateCamiseta> {
            CreateEditScreen(
                onBack = { navController.navigateUp() },
                bottomNavigation = bottomNavigation
            )
        }

        composable<Screen.Profile> {
            ProfileScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login) { popUpTo(0) { inclusive = true } }
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile) { launchSingleTop = true }
                },
                onNavigateToGestionPublicaciones = {
                    navController.navigate(Screen.GestionPublicaciones)
                },
                onNavigateToAdminPedidos = {
                    navController.navigate(Screen.AdminPedidos)
                },
                onNavigateToMisPedidos = {
                    navController.navigate(Screen.MisPedidos)
                },
                onNavigateToAddresses = {
                    navController.navigate(Screen.AddressList)
                },
                onNavigateToPayments = {
                    navController.navigate(Screen.PaymentList)
                },
                onBack = { navController.navigateUp() },
                bottomNavigation = bottomNavigation
            )
        }

        composable<Screen.EditProfile> {
            com.example.golazo_store.presentation.profile.edit.EditProfileScreen(
                onBack = { navController.navigateUp() },
                onAccountDeleted = {
                    navController.navigate(Screen.Login) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable<Screen.GestionPublicaciones> {
            CreateListScreen(
                onNavigateToCreate = { navController.navigate(Screen.CreateCamiseta()) },
                onNavigateToEdit = { id -> navController.navigate(Screen.CreateCamiseta(id)) },
                onBack = { navController.navigateUp() },
                bottomNavigation = bottomNavigation
            )
        }

        composable<Screen.AdminPedidos> {
            AdminPedidosScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToDetail = { id -> navController.navigate(Screen.AdminPedidoDetail(id)) }
            )
        }

        composable<Screen.AdminPedidoDetail> {
            AdminPedidoDetailScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<Screen.Cart> {
            CartScreen(
                onBack = { navController.navigateUp() },
                onCheckout = { 
                    navController.navigate(Screen.Checkout) {
                        launchSingleTop = true
                    } 
                }
            )
        }
        composable<Screen.AddressList> {
            AddressListScreen(
                onNavigateToEdit = { id -> 
                    navController.navigate(Screen.AddressEdit(id))
                },
                onBack = { navController.navigateUp() }
            )
        }
        composable<Screen.AddressEdit> {
            AddressEditScreen(
                onBack = { navController.navigateUp() }
            )
        }
        composable<Screen.PaymentList> {
            PaymentListScreen(
                onNavigateToAdd = {
                    navController.navigate(Screen.PaymentAdd)
                },
                onBack = { navController.navigateUp() }
            )
        }
        composable<Screen.PaymentAdd> {
            PaymentAddScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Screen.Checkout> { backStackEntry ->
            val viewModel = hiltViewModel<CheckoutViewModel>()
            val selectedAddressId = backStackEntry.savedStateHandle.get<Int>("selected_address_id")
            val selectedPaymentId = backStackEntry.savedStateHandle.get<Int>("selected_payment_id")

            LaunchedEffect(selectedAddressId) {
                if (selectedAddressId != null) {
                    viewModel.selectAddress(selectedAddressId)
                    backStackEntry.savedStateHandle.remove<Int>("selected_address_id")
                }
            }
            LaunchedEffect(selectedPaymentId) {
                if (selectedPaymentId != null) {
                    viewModel.selectPayment(selectedPaymentId)
                    backStackEntry.savedStateHandle.remove<Int>("selected_payment_id")
                }
            }

            CheckoutScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSuccess = { orderNumber ->
                    navController.navigate(Screen.CheckoutSuccess(orderNumber)) {
                        popUpTo(Screen.Cart) { inclusive = true }
                    }
                },
                onChangeAddress = { id ->
                    navController.navigate(Screen.ChangeAddress(id ?: -1)) {
                        launchSingleTop = true
                    }
                },
                onChangePayment = { id ->
                    navController.navigate(Screen.ChangePayment(id ?: -1)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Screen.ChangeAddress> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.ChangeAddress>()
            ChangeAddressScreen(
                onBack = { navController.navigateUp() },
                currentSelectedId = args.currentId,
                onAddressSelected = { selectedId ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("selected_address_id", selectedId)
                    navController.navigateUp()
                }
            )
        }

        composable<Screen.ChangePayment> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.ChangePayment>()
            ChangePaymentScreen(
                onBack = { navController.navigateUp() },
                currentSelectedId = args.currentId,
                onPaymentSelected = { selectedId ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("selected_payment_id", selectedId)
                    navController.navigateUp()
                }
            )
        }

        composable<Screen.CheckoutSuccess> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.CheckoutSuccess>()
            val viewModel = hiltViewModel<CheckoutSuccessViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()

            CheckoutSuccessScreen(
                state = state,
                onViewOrders = {
                    navController.navigate(Screen.MisPedidos) {
                        popUpTo(Screen.Home) { inclusive = false }
                    }
                },
                onContinueShopping = {
                    navController.navigate(Screen.Home) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = {
                    navController.navigate(Screen.Home) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.MisPedidos> {
            MisPedidosScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToTrack = { id -> navController.navigate(Screen.RastrearPedido(id)) },
                onNavigateToDetail = { id -> navController.navigate(Screen.RastrearPedido(id)) } // Route to Track for now
            )
        }

        composable<Screen.RastrearPedido> {
            RastrearPedidoScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}
