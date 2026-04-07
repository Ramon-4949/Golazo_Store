package com.example.golazo_store.domain.usecase.validation

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)
