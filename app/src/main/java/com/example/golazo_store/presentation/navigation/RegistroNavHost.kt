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
import com.example.golazo_store.presentation.checkout.CheckoutScreen
import com.example.golazo_store.presentation.checkout.CheckoutSuccessScreen
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


@Composable
fun RegistroNavHost(
    navController: NavHostController = rememberNavController()
) {
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
        startDestination = Screen.Login
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
                onBack = { navController.navigateUp() }
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
                    navController.navigate(Screen.Login) { popUpTo(0) }
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
                onCheckout = { navController.navigate(Screen.Checkout) }
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

        composable<Screen.Checkout> {
            CheckoutScreen(
                onBack = { navController.popBackStack() },
                onSuccess = { orderNumber ->
                    navController.navigate(Screen.CheckoutSuccess(orderNumber)) {
                        popUpTo(Screen.Cart) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.CheckoutSuccess> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.CheckoutSuccess>()
            CheckoutSuccessScreen(
                orderNumber = args.orderNumber,
                onViewOrders = {
                    navController.navigate(Screen.Home) {
                        popUpTo(0) { inclusive = true }
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
