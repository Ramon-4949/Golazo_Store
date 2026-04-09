package com.example.golazo_store.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.golazo_store.presentation.navigation.Screen


@Composable
fun GolazoBottomNavigation(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == Screen.Home::class.qualifiedName,
            onClick = onNavigateToHome,
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { 
                Text(
                    "Inicio", 
                    fontWeight = if (currentRoute == Screen.Home::class.qualifiedName) FontWeight.Bold else FontWeight.Normal
                ) 
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.surface,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Categories::class.qualifiedName,
            onClick = onNavigateToCategories,
            icon = { Icon(Icons.Filled.GridView, contentDescription = "Categorías") },
            label = { 
                Text(
                    "Categorías",
                    fontWeight = if (currentRoute == Screen.Categories::class.qualifiedName) FontWeight.Bold else FontWeight.Normal
                ) 
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.surface,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Favorites::class.qualifiedName,
            onClick = onNavigateToFavorites,
            icon = { 
                Icon(
                    if (currentRoute == Screen.Favorites::class.qualifiedName) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, 
                    contentDescription = "Favoritos"
                ) 
            },
            label = { 
                Text(
                    "Favoritos",
                    fontWeight = if (currentRoute == Screen.Favorites::class.qualifiedName) FontWeight.Bold else FontWeight.Normal
                ) 
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.surface,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Profile::class.qualifiedName,
            onClick = onNavigateToProfile,
            icon = { Icon(Icons.Filled.Person, contentDescription = "Mi Perfil") },
            label = { 
                Text(
                    "Mi Perfil",
                    fontWeight = if (currentRoute == Screen.Profile::class.qualifiedName) FontWeight.Bold else FontWeight.Normal
                ) 
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.surface,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}



