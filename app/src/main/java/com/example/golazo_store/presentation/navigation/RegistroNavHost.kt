package com.example.golazo_store.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.golazo_store.presentation.list.RegistroListScreen
import com.example.golazo_store.presentation.detail.RegistroDetailScreen
import com.example.golazo_store.presentation.home.HomeScreen
import com.example.golazo_store.presentation.create.CreateScreen
import com.example.golazo_store.presentation.categories.CategoriesScreen
import com.example.golazo_store.presentation.favorites.FavoritesScreen
import com.example.golazo_store.presentation.components.GolazoBottomNavigation

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
                navController.navigate(Screen.CreateCamiseta) {
                    popUpTo(Screen.Home) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.RegistroList> {
            RegistroListScreen(
                onNavigateToDetail = { id -> navController.navigate(Screen.RegistroDetail(id)) },
                onNavigateToCreate = { navController.navigate(Screen.RegistroDetail(0)) }
            )
        }

        composable<Screen.RegistroDetail> {
            RegistroDetailScreen(
                onBack = { navController.navigateUp() }
            )
        }

        composable<Screen.Home> {
            HomeScreen(
                onNavigateToCreate = { navController.navigate(Screen.CreateCamiseta) },
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
                bottomNavigation = bottomNavigation
            )
        }

        composable<Screen.CreateCamiseta> {
            CreateScreen(
                onBack = { navController.navigate(Screen.Home) },
                bottomNavigation = bottomNavigation
            )
        }
    }
}