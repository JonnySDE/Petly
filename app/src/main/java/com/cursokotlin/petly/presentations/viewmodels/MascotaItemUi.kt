package com.cursokotlin.petly.presentations.viewmodels

import com.cursokotlin.petly.domain.model.EstadoAlimentacion
import com.cursokotlin.petly.domain.model.Mascota
import java.time.Clock
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

data class MascotaItemUi(
    val id: String,
    val nombre: String,
    val especie: String,
    val edadAnios: Int,
    val estaVacunado: Boolean,
    val yaComio: Boolean,
    val comidaResumen: String?,
)

fun Mascota.toItemUi(clock: Clock): MascotaItemUi {
    val yaComio = estadoAlimentacion(clock) == EstadoAlimentacion.YaComio
    val resumen = if (yaComio) {
        ultimaAlimentacion?.let {
            val hora = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                .format(it.fechaHora.atZone(clock.zone))
            "${it.tipoAlimento} (${it.cantidad}) • $hora"
        }
    } else {
        null
    }
    return MascotaItemUi(
        id = id,
        nombre = nombre,
        especie = especie,
        edadAnios = edadAnios,
        estaVacunado = estaVacunado,
        yaComio = yaComio,
        comidaResumen = resumen,
    )
}

fun inicialesDe(nombre: String): String =
    nombre.trim()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }