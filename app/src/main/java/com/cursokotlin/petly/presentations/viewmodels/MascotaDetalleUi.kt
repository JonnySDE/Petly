package com.cursokotlin.petly.presentations.viewmodels

import com.cursokotlin.petly.domain.model.EstadoAlimentacion
import com.cursokotlin.petly.domain.model.Mascota
import java.time.Clock
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/** Lo que muestra la ficha de una mascota: fechas y horas ya formateadas, sin lógica en la pantalla. */
data class MascotaDetalleUi(
    val id: String,
    val nombre: String,
    val especie: String,
    val edadAnios: Int,
    val estaVacunado: Boolean,
    /** Fecha legible de la última visita al veterinario; null si nunca ha ido. */
    val ultimaVisita: String?,
    /** True solo si el último registro de alimentación es de hoy. */
    val yaComio: Boolean,
    /** Último registro de alimentación, de hoy o de otro día; null si no hay ninguno. */
    val alimentacion: AlimentacionDetalleUi?,
)

data class AlimentacionDetalleUi(
    val tipo: String,
    val cantidad: String,
    val fecha: String,
    val hora: String,
)

fun Mascota.toDetalleUi(clock: Clock): MascotaDetalleUi {
    val formatoFecha = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    val formatoHora = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    return MascotaDetalleUi(
        id = id,
        nombre = nombre,
        especie = especie,
        edadAnios = edadAnios,
        estaVacunado = estaVacunado,
        ultimaVisita = fechaUltimaVisita?.let { formatoFecha.format(it) },
        yaComio = estadoAlimentacion(clock) == EstadoAlimentacion.YaComio,
        alimentacion = ultimaAlimentacion?.let {
            val momento = it.fechaHora.atZone(clock.zone)
            AlimentacionDetalleUi(
                tipo = it.tipoAlimento,
                cantidad = it.cantidad,
                fecha = formatoFecha.format(momento),
                hora = formatoHora.format(momento),
            )
        },
    )
}