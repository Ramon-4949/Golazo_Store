package com.example.golazo_store.presentation.categories

sealed interface CategoriesEvent {
    data class UpdateSearchQuery(val query: String) : CategoriesEvent
    data class ClickCategory(val categoryTitle: String) : CategoriesEvent
    object ClickMenu : CategoriesEvent
    object ClickCart : CategoriesEvent
    object ClickViewAll : CategoriesEvent

}
