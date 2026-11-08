package com.cursokotlin.petly.data.mapper

import com.cursokotlin.petly.data.dto.AlimentacionRequestDto
import com.cursokotlin.petly.data.dto.MascotaDto
import com.cursokotlin.petly.data.dto.MascotaRequestDto
import com.cursokotlin.petly.domain.model.Mascota
import com.cursokotlin.petly.domain.model.MascotaInput
import com.cursokotlin.petly.domain.model.UltimaAlimentacion
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit


fun MascotaDto.toDomain(): Mascota {
    val alimentacion = if (tipoAlimento != null && cantidadAlimento != null && fechaHoraUltimaAlimentacion != null) {
        UltimaAlimentacion(
            tipoAlimento = tipoAlimento,
            cantidad = cantidadAlimento,
            fechaHora = Instant.parse(fechaHoraUltimaAlimentacion),
        )
    } else {
        null
    }
    return Mascota(
        id = id,
        nombre = nombre,
        especie = especie,
        edadAnios = edadAnios,
        estaVacunado = estaVacunado,
        fechaUltimaVisita = fechaUltimaVisita?.let(LocalDate::parse),
        ultimaAlimentacion = alimentacion,
    )
}

/** Dominio → DTO de creación y edición. Los textos se recortan. */
fun MascotaInput.toRequestDto(): MascotaRequestDto = MascotaRequestDto(
    nombre = nombre.trim(),
    especie = especie.trim(),
    edadAnios = edadAnios,
    estaVacunado = estaVacunado,
    fechaUltimaVisita = fechaUltimaVisita?.toString(),
)

/** Dominio → DTO del PATCH. La hora se envía en UTC y sin fracciones de segundo. */
fun UltimaAlimentacion.toRequestDto(): AlimentacionRequestDto = AlimentacionRequestDto(
    tipoAlimento = tipoAlimento.trim(),
    cantidadAlimento = cantidad.trim(),
    fechaHoraUltimaAlimentacion = fechaHora.truncatedTo(ChronoUnit.SECONDS).toString(),
)