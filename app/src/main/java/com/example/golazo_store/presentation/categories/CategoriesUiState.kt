package com.example.golazo_store.presentation.categories

import androidx.compose.ui.graphics.Color

import androidx.annotation.DrawableRes

data class CategoryDemo(
    val title: String,
    val subtitle: String,
    @DrawableRes val imageResId: Int,
    val badge: String? = null
)

data class CategoriesUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val categories: List<CategoryDemo> = emptyList(),
    val error: String? = null
)


