package com.cursokotlin.petly.domain.validation

import java.time.LocalDate

/** Reglas del formulario de mascota y de alimentación. Cada función devuelve null si es válido. */
object MascotaValidator {
    const val MAX_TEXTO = 50
    const val MAX_CANTIDAD = 30
    const val EDAD_MIN = 0
    const val EDAD_MAX = 30

    fun validarNombre(nombre: String): ValidationError? = validarTexto(nombre, MAX_TEXTO)

    fun validarEspecie(especie: String): ValidationError? = validarTexto(especie, MAX_TEXTO)

    /** La edad llega como texto porque viene de un campo de la pantalla. */
    fun validarEdad(edad: String): ValidationError? {
        val texto = edad.trim()
        if (texto.isEmpty()) return ValidationError.Required
        val numero = texto.toIntOrNull() ?: return ValidationError.InvalidNumber
        return if (numero in EDAD_MIN..EDAD_MAX) null else ValidationError.AgeOutOfRange
    }

    /** La fecha de última visita es opcional; si existe, no puede ser posterior a hoy. */
    fun validarFechaVisita(fecha: LocalDate?, hoy: LocalDate): ValidationError? =
        if (fecha != null && fecha.isAfter(hoy)) ValidationError.FutureDate else null

    fun validarTipoAlimento(tipo: String): ValidationError? = validarTexto(tipo, MAX_TEXTO)

    fun validarCantidad(cantidad: String): ValidationError? = validarTexto(cantidad, MAX_CANTIDAD)

    private fun validarTexto(valor: String, maximo: Int): ValidationError? = when {
        valor.isBlank() -> ValidationError.Required
        valor.trim().length > maximo -> ValidationError.TooLong
        else -> null
    }
}