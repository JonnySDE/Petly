package com.cursokotlin.petly.presentations.viewmodels

import com.cursokotlin.petly.domain.model.Mascota
import com.cursokotlin.petly.domain.model.UltimaAlimentacion
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MascotaItemUiTest {
    // 14:00 del 5 de noviembre en Chiapas (UTC-6).
    private val reloj = Clock.fixed(Instant.parse("2026-11-05T20:00:00Z"), ZoneId.of("America/Mexico_City"))

    private fun mascota(comida: String?) = Mascota(
        id = "m1",
        nombre = "Luna",
        especie = "Gato",
        edadAnios = 3,
        estaVacunado = true,
        fechaUltimaVisita = null,
        ultimaAlimentacion = comida?.let { UltimaAlimentacion("Croquetas", "250 g", Instant.parse(it)) },
    )

    @Test
    fun conComidaDeHoyYaComioYMuestraElResumen() {
        val item = mascota("2026-11-05T15:00:00Z").toItemUi(reloj)

        assertTrue(item.yaComio)
        assertTrue(item.comidaResumen.orEmpty().startsWith("Croquetas (250 g) • "))
    }

    @Test
    fun conComidaDeAyerNoComioYNoHayResumen() {
        val item = mascota("2026-11-04T15:00:00Z").toItemUi(reloj)

        assertFalse(item.yaComio)
        assertNull(item.comidaResumen)
    }

    @Test
    fun sinRegistroNoComioYNoHayResumen() {
        val item = mascota(null).toItemUi(reloj)

        assertFalse(item.yaComio)
        assertNull(item.comidaResumen)
    }

    @Test
    fun copiaLosDatosDeLaMascota() {
        val item = mascota(null).toItemUi(reloj)

        assertEquals("m1", item.id)
        assertEquals("Luna", item.nombre)
        assertEquals("Gato", item.especie)
        assertEquals(3, item.edadAnios)
        assertTrue(item.estaVacunado)
    }

    @Test
    fun inicialesDeDosPalabras() {
        assertEquals("JR", inicialesDe("Jonathan Rodriguez"))
        assertEquals("AL", inicialesDe("  ana   lópez  gómez "))
    }

    @Test
    fun inicialesDeUnaPalabraOVacio() {
        assertEquals("A", inicialesDe("Ana"))
        assertEquals("", inicialesDe(""))
        assertEquals("", inicialesDe("   "))
    }
}