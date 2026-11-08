package com.cursokotlin.petly.data.mapper

import com.cursokotlin.petly.data.dto.AlimentacionRequestDto
import com.cursokotlin.petly.data.dto.MascotaDto
import com.cursokotlin.petly.data.dto.MascotaRequestDto
import com.cursokotlin.petly.domain.model.Mascota
import com.cursokotlin.petly.domain.model.MascotaInput
import com.cursokotlin.petly.domain.model.UltimaAlimentacion
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeParseException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test

class MascotaMapperTest {

    private fun dto(
        visita: String? = "2020-01-01",
        tipo: String? = "Croquetas",
        cantidad: String? = "250 g",
        hora: String? = "2026-11-05T14:30:00Z",
    ) = MascotaDto(
        id = "m1",
        nombre = "Luna",
        especie = "Gato",
        edadAnios = 3,
        estaVacunado = true,
        fechaUltimaVisita = visita,
        tipoAlimento = tipo,
        cantidadAlimento = cantidad,
        fechaHoraUltimaAlimentacion = hora,
    )

    @Test
    fun dtoCompletoSeConvierteADominio() {
        val esperado = Mascota(
            id = "m1",
            nombre = "Luna",
            especie = "Gato",
            edadAnios = 3,
            estaVacunado = true,
            fechaUltimaVisita = LocalDate.of(2020, 1, 1),
            ultimaAlimentacion = UltimaAlimentacion("Croquetas", "250 g", Instant.parse("2026-11-05T14:30:00Z")),
        )
        assertEquals(esperado, dto().toDomain())
    }

    @Test
    fun sinDatosDeAlimentacionQuedaNull() {
        assertNull(dto(tipo = null, cantidad = null, hora = null).toDomain().ultimaAlimentacion)
    }

    @Test
    fun alimentacionIncompletaSeIgnora() {
        assertNull(dto(hora = null).toDomain().ultimaAlimentacion)
        assertNull(dto(tipo = null).toDomain().ultimaAlimentacion)
        assertNull(dto(cantidad = null).toDomain().ultimaAlimentacion)
    }

    @Test
    fun fechaDeVisitaNulaSeRespeta() {
        assertNull(dto(visita = null).toDomain().fechaUltimaVisita)
    }

    @Test
    fun laHoraConFraccionesDeSegundoSeLee() {
        val mascota = dto(hora = "2026-11-05T14:30:00.123456Z").toDomain()
        assertEquals(Instant.parse("2026-11-05T14:30:00.123456Z"), mascota.ultimaAlimentacion?.fechaHora)
    }

    @Test
    fun unaFechaMalFormadaLanzaDateTimeParseException() {
        try {
            dto(visita = "ayer").toDomain()
            fail("debía lanzar DateTimeParseException")
        } catch (e: DateTimeParseException) {
            // esperado: safeApiCall lo traduce a DataError.Serialization
        }
        try {
            dto(hora = "2026-11-05 14:30").toDomain()
            fail("debía lanzar DateTimeParseException")
        } catch (e: DateTimeParseException) {
            // esperado
        }
    }

    @Test
    fun elRequestRecortaTextosYFormateaLaFecha() {
        val input = MascotaInput(" Luna ", " Gato ", 3, true, LocalDate.of(2026, 5, 12))
        assertEquals(
            MascotaRequestDto("Luna", "Gato", 3, true, "2026-05-12"),
            input.toRequestDto(),
        )
    }

    @Test
    fun elRequestSinFechaDeVisitaViajaNulo() {
        val input = MascotaInput("Luna", "Gato", 3, false, null)
        assertNull(input.toRequestDto().fechaUltimaVisita)
    }

    @Test
    fun laAlimentacionSeEnviaEnUtcSinFraccionesYRecortada() {
        val alimentacion = UltimaAlimentacion(" Croquetas ", " 250 g ", Instant.parse("2026-11-05T14:30:00.987654Z"))
        assertEquals(
            AlimentacionRequestDto("Croquetas", "250 g", "2026-11-05T14:30:00Z"),
            alimentacion.toRequestDto(),
        )
    }
}