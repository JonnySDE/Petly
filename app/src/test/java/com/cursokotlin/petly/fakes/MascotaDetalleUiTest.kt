package com.cursokotlin.petly.presentations.viewmodels

import com.cursokotlin.petly.domain.model.Mascota
import com.cursokotlin.petly.domain.model.UltimaAlimentacion
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MascotaDetalleUiTest {
    // 14:00 del 5 de noviembre en Chiapas (UTC-6).
    private val reloj = Clock.fixed(Instant.parse("2026-11-05T20:00:00Z"), ZoneId.of("America/Mexico_City"))

    private fun mascota(
        visita: LocalDate? = null,
        comida: String? = null,
    ) = Mascota(
        id = "m1",
        nombre = "Luna",
        especie = "Gato",
        edadAnios = 3,
        estaVacunado = true,
        fechaUltimaVisita = visita,
        ultimaAlimentacion = comida?.let { UltimaAlimentacion("Croquetas", "250 g", Instant.parse(it)) },
    )

    @Test
    fun conComidaDeHoyYaComioYMuestraSusDatos() {
        val detalle = mascota(comida = "2026-11-05T15:00:00Z").toDetalleUi(reloj)

        assertTrue(detalle.yaComio)
        assertEquals("Croquetas", detalle.alimentacion?.tipo)
        assertEquals("250 g", detalle.alimentacion?.cantidad)
        assertTrue(detalle.alimentacion?.hora.orEmpty().isNotEmpty())
    }

    @Test
    fun conComidaDeAyerEstaPendienteYConservaElUltimoRegistro() {
        val detalle = mascota(comida = "2026-11-04T15:00:00Z").toDetalleUi(reloj)

        assertFalse(detalle.yaComio)
        assertEquals("Croquetas", detalle.alimentacion?.tipo)
    }

    @Test
    fun laFechaDeLaComidaSeCuentaEnLaZonaLocalNoEnUtc() {
        // 05:30 UTC del 6 de noviembre es todavía el 5 a las 23:30 en Chiapas.
        val formato = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        val detalle = mascota(comida = "2026-11-06T05:30:00Z").toDetalleUi(reloj)

        assertEquals(formato.format(LocalDate.of(2026, 11, 5)), detalle.alimentacion?.fecha)
    }

    @Test
    fun sinRegistroNoHayAlimentacionYEstaPendiente() {
        val detalle = mascota().toDetalleUi(reloj)

        assertFalse(detalle.yaComio)
        assertNull(detalle.alimentacion)
    }

    @Test
    fun laUltimaVisitaSeFormateaOQuedaNula() {
        val formato = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

        assertEquals(
            formato.format(LocalDate.of(2026, 5, 12)),
            mascota(visita = LocalDate.of(2026, 5, 12)).toDetalleUi(reloj).ultimaVisita,
        )
        assertNull(mascota(visita = null).toDetalleUi(reloj).ultimaVisita)
    }

    @Test
    fun copiaLosDatosBasicos() {
        val detalle = mascota().toDetalleUi(reloj)

        assertEquals("m1", detalle.id)
        assertEquals("Luna", detalle.nombre)
        assertEquals("Gato", detalle.especie)
        assertEquals(3, detalle.edadAnios)
        assertTrue(detalle.estaVacunado)
    }
}