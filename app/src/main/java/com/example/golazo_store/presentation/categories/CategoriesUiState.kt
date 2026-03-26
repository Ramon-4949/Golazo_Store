package com.example.golazo_store.presentation.categories

import androidx.compose.ui.graphics.Color

data class CategoryDemo(
    val title: String,
    val subtitle: String,
    val backgroundColor: Color,
    val badge: String? = null
)

data class CategoriesUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val categories: List<CategoryDemo> = emptyList(),
    val error: String? = null
)
