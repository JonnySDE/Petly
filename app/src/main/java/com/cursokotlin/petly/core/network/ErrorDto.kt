package com.cursokotlin.petly.core.network

import kotlinx.serialization.Serializable

@Serializable
data class ErrorDto(
    val codigo: String,
    val mensaje: String = "",
    val campos: List<CampoErrorDto>? = null,
)

@Serializable
data class CampoErrorDto(
    val campo: String,
    val mensaje: String,
)