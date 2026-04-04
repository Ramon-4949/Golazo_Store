package com.example.golazo_store.presentation.CreateEdit.edit

sealed interface CreateEditEvent {
    data class OnNombreChange(val nombre: String) : CreateEditEvent
    data class OnDescripcionChange(val descripcion: String) : CreateEditEvent
    data class OnPrecioChange(val precio: String) : CreateEditEvent
    data class OnStockSChange(val stock: String) : CreateEditEvent
    data class OnStockMChange(val stock: String) : CreateEditEvent
    data class OnStockLChange(val stock: String) : CreateEditEvent
    data class OnStockXLChange(val stock: String) : CreateEditEvent
    data class OnStock2XLChange(val stock: String) : CreateEditEvent
    data class OnCategoriaSelected(val categoriaId: Int) : CreateEditEvent
    data class OnImagePicked(val uri: android.net.Uri?) : CreateEditEvent
    object SaveProduct : CreateEditEvent
    object ClearMessages : CreateEditEvent
}


