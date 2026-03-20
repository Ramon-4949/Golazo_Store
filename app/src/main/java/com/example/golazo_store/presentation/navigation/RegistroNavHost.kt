package com.example.golazo_store.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.golazo_store.presentation.list.RegistroListScreen
import com.example.golazo_store.presentation.detail.RegistroDetailScreen

@Composable
fun RegistroNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.RegistroList
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
    }
}