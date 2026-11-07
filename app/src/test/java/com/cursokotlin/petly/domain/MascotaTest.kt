package com.cursokotlin.petly.domain

import com.cursokotlin.petly.domain.model.EstadoAlimentacion
import com.cursokotlin.petly.domain.model.Mascota
import com.cursokotlin.petly.domain.model.UltimaAlimentacion
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class MascotaTest {
    // Chiapas usa UTC-6 todo el año.
    private val zona = ZoneId.of("America/Mexico_City")

    private fun ahora(iso: String) = Clock.fixed(Instant.parse(iso), zona)

    private fun mascotaCon(comida: String?) = Mascota(
        id = "1",
        nombre = "Luna",
        especie = "Gato",
        edadAnios = 3,
        estaVacunado = true,
        fechaUltimaVisita = null,
        ultimaAlimentacion = comida?.let { UltimaAlimentacion("Croquetas", "250 g", Instant.parse(it)) },
    )

    @Test
    fun sinRegistroEstaPendiente() {
        val estado = mascotaCon(null).estadoAlimentacion(ahora("2026-11-05T20:00:00Z"))
        assertEquals(EstadoAlimentacion.Pendiente, estado)
    }

    @Test
    fun registroDeHoyYaComio() {
        val estado = mascotaCon("2026-11-05T15:00:00Z").estadoAlimentacion(ahora("2026-11-05T20:00:00Z"))
        assertEquals(EstadoAlimentacion.YaComio, estado)
    }

    @Test
    fun registroDeAyerEstaPendiente() {
        val estado = mascotaCon("2026-11-04T15:00:00Z").estadoAlimentacion(ahora("2026-11-05T20:00:00Z"))
        assertEquals(EstadoAlimentacion.Pendiente, estado)
    }

    @Test
    fun cercaDeMedianocheUsaLaZonaLocalYNoUtc() {
        // 17:30 y 22:00 del 5 de noviembre en Chiapas, aunque en UTC sean días distintos.
        val estado = mascotaCon("2026-11-05T23:30:00Z").estadoAlimentacion(ahora("2026-11-06T04:00:00Z"))
        assertEquals(EstadoAlimentacion.YaComio, estado)
    }

    @Test
    fun pasadaLaMedianocheLocalVuelveAPendiente() {
        // 23:30 del 5 y 01:00 del 6 en Chiapas, aunque en UTC sean el mismo día.
        val estado = mascotaCon("2026-11-06T05:30:00Z").estadoAlimentacion(ahora("2026-11-06T07:00:00Z"))
        assertEquals(EstadoAlimentacion.Pendiente, estado)
    }
}