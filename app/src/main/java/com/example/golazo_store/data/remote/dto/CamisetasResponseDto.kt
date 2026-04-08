package com.example.golazo_store.data.remote.dto

import com.squareup.moshi.Json

data class CamisetasResponseDto(
    @Json(name = "value")
    val value: List<CamisetaDto>,
    @Json(name = "Count")
    val count: Int? = null
)
