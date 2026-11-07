package com.cursokotlin.petly.domain.validation

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MascotaValidatorTest {
    private val hoy = LocalDate.of(2026, 11, 5)

    @Test
    fun nombreYEspecieObligatoriosConLimite() {
        assertEquals(ValidationError.Required, MascotaValidator.validarNombre("  "))
        assertEquals(ValidationError.TooLong, MascotaValidator.validarNombre("a".repeat(51)))
        assertNull(MascotaValidator.validarNombre("Luna"))
        assertEquals(ValidationError.Required, MascotaValidator.validarEspecie(""))
        assertNull(MascotaValidator.validarEspecie("Gato"))
    }

    @Test
    fun edadEntreCeroYTreinta() {
        assertEquals(ValidationError.Required, MascotaValidator.validarEdad(""))
        assertEquals(ValidationError.InvalidNumber, MascotaValidator.validarEdad("abc"))
        assertEquals(ValidationError.InvalidNumber, MascotaValidator.validarEdad("3.5"))
        assertEquals(ValidationError.AgeOutOfRange, MascotaValidator.validarEdad("-1"))
        assertEquals(ValidationError.AgeOutOfRange, MascotaValidator.validarEdad("31"))
        assertNull(MascotaValidator.validarEdad("0"))
        assertNull(MascotaValidator.validarEdad("30"))
        assertNull(MascotaValidator.validarEdad(" 4 "))
    }

    @Test
    fun fechaDeVisitaEsOpcionalYNoPuedeSerFutura() {
        assertNull(MascotaValidator.validarFechaVisita(null, hoy))
        assertNull(MascotaValidator.validarFechaVisita(hoy, hoy))
        assertNull(MascotaValidator.validarFechaVisita(hoy.minusDays(1), hoy))
        assertEquals(ValidationError.FutureDate, MascotaValidator.validarFechaVisita(hoy.plusDays(1), hoy))
    }

    @Test
    fun alimentacionTipoYCantidadObligatorios() {
        assertEquals(ValidationError.Required, MascotaValidator.validarTipoAlimento(" "))
        assertEquals(ValidationError.TooLong, MascotaValidator.validarTipoAlimento("a".repeat(51)))
        assertNull(MascotaValidator.validarTipoAlimento("Croquetas"))
        assertEquals(ValidationError.Required, MascotaValidator.validarCantidad(""))
        assertEquals(ValidationError.TooLong, MascotaValidator.validarCantidad("a".repeat(31)))
        assertNull(MascotaValidator.validarCantidad("250 g"))
    }
}