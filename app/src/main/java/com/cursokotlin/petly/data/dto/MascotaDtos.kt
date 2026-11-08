package com.cursokotlin.petly.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MascotaDto(
    val id: String,
    val nombre: String,
    val especie: String,
    val edadAnios: Int,
    val estaVacunado: Boolean,
    val fechaUltimaVisita: String? = null,
    val tipoAlimento: String? = null,
    val cantidadAlimento: String? = null,
    val fechaHoraUltimaAlimentacion: String? = null,
)

/** Cuerpo de POST y PUT: datos generales, sin id ni alimentación. */
@Serializable
data class MascotaRequestDto(
    val nombre: String,
    val especie: String,
    val edadAnios: Int,
    val estaVacunado: Boolean,
    val fechaUltimaVisita: String? = null,
)

/** Cuerpo del PATCH que registra la última alimentación. */
@Serializable
data class AlimentacionRequestDto(
    val tipoAlimento: String,
    val cantidadAlimento: String,
    val fechaHoraUltimaAlimentacion: String,
)