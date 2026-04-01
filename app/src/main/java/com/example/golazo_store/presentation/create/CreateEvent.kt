package com.example.golazo_store.presentation.create

sealed interface CreateEvent {
    data class OnNombreChange(val nombre: String) : CreateEvent
    data class OnDescripcionChange(val descripcion: String) : CreateEvent
    data class OnPrecioChange(val precio: String) : CreateEvent
    data class OnStockSChange(val stock: String) : CreateEvent
    data class OnStockMChange(val stock: String) : CreateEvent
    data class OnStockLChange(val stock: String) : CreateEvent
    data class OnStockXLChange(val stock: String) : CreateEvent
    data class OnStock2XLChange(val stock: String) : CreateEvent
    data class OnCategoriaSelected(val categoriaId: Int) : CreateEvent
    data class OnImagePicked(val uri: android.net.Uri?) : CreateEvent
    object SaveProduct : CreateEvent
    object ClearMessages : CreateEvent
}
