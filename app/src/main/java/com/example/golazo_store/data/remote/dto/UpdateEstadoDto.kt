package com.example.golazo_store.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateEstadoDto(
    val estado: String
)
