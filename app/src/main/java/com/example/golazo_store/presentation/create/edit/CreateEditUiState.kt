package com.example.golazo_store.presentation.CreateEdit.edit

import com.example.golazo_store.domain.model.Categoria

data class CreateEditUiState(
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val stockS: String = "",
    val stockM: String = "",
    val stockL: String = "",
    val stockXL: String = "",
    val stock2XL: String = "",
    val categorias: List<Categoria> = emptyList(),
    val selectedCategoriaId: Int? = null,
    val imageUri: android.net.Uri? = null,
    val originalImageUrl: String? = null,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val isEditing: Boolean = false
)


