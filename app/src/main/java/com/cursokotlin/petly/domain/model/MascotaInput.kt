package com.cursokotlin.petly.domain.model

import java.time.LocalDate

/** Datos generales que el usuario captura al crear o editar una mascota. */
data class MascotaInput(
    val nombre: String,
    val especie: String,
    val edadAnios: Int,
    val estaVacunado: Boolean,
    val fechaUltimaVisita: LocalDate?,
)