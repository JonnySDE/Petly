package com.cursokotlin.petly.domain.model

import java.time.Clock
import java.time.Instant
import java.time.LocalDate

data class Mascota(
    val id: String,
    val nombre: String,
    val especie: String,
    val edadAnios: Int,
    val estaVacunado: Boolean,
    val fechaUltimaVisita: LocalDate?,
    val ultimaAlimentacion: UltimaAlimentacion?,
) {
    /**
     * "Ya comió" si el último registro cae en el día de hoy según la zona horaria del reloj;
     * en cualquier otro caso (sin registro o de otro día), "Pendiente".
     * Recibe el reloj para poder probarlo con fechas fijas.
     */
    fun estadoAlimentacion(clock: Clock): EstadoAlimentacion {
        val diaDeLaComida = ultimaAlimentacion?.fechaHora?.atZone(clock.zone)?.toLocalDate()
        return if (diaDeLaComida == LocalDate.now(clock)) {
            EstadoAlimentacion.YaComio
        } else {
            EstadoAlimentacion.Pendiente
        }
    }
}

/** Último registro de alimentación (no es un historial). */
data class UltimaAlimentacion(
    val tipoAlimento: String,
    val cantidad: String,
    val fechaHora: Instant,
)

enum class EstadoAlimentacion { YaComio, Pendiente }