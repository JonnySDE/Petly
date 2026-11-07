package com.cursokotlin.petly.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthValidatorTest {
    @Test
    fun nombreObligatorioYConLimite() {
        assertEquals(ValidationError.Required, AuthValidator.validarNombre("   "))
        assertEquals(ValidationError.TooLong, AuthValidator.validarNombre("a".repeat(51)))
        assertNull(AuthValidator.validarNombre("Ana López"))
    }

    @Test
    fun correoDebeTenerFormatoValido() {
        assertEquals(ValidationError.Required, AuthValidator.validarCorreo(""))
        assertEquals(ValidationError.InvalidEmail, AuthValidator.validarCorreo("ana"))
        assertEquals(ValidationError.InvalidEmail, AuthValidator.validarCorreo("ana@"))
        assertEquals(ValidationError.InvalidEmail, AuthValidator.validarCorreo("ana@correo"))
        assertNull(AuthValidator.validarCorreo("ana@correo.com"))
        assertNull(AuthValidator.validarCorreo(" jonny.dev@upchiapas.edu.mx "))
    }

    @Test
    fun contrasenaNuevaMinimoOchoCaracteres() {
        assertEquals(ValidationError.Required, AuthValidator.validarContrasenaNueva(""))
        assertEquals(ValidationError.PasswordTooShort, AuthValidator.validarContrasenaNueva("1234567"))
        assertNull(AuthValidator.validarContrasenaNueva("12345678"))
    }

    @Test
    fun contrasenaNuevaNoPuedePasarDe72Bytes() {
        assertNull(AuthValidator.validarContrasenaNueva("a".repeat(72)))
        assertEquals(ValidationError.PasswordTooLong, AuthValidator.validarContrasenaNueva("a".repeat(73)))
        // 40 letras con acento ocupan 80 bytes aunque sean solo 40 caracteres.
        assertEquals(ValidationError.PasswordTooLong, AuthValidator.validarContrasenaNueva("é".repeat(40)))
    }

    @Test
    fun confirmacionDebeCoincidir() {
        assertEquals(ValidationError.Required, AuthValidator.validarConfirmacion("miClave123", ""))
        assertEquals(ValidationError.PasswordMismatch, AuthValidator.validarConfirmacion("miClave123", "otraClave"))
        assertNull(AuthValidator.validarConfirmacion("miClave123", "miClave123"))
    }

    @Test
    fun loginSoloExigeContrasenaNoVacia() {
        assertEquals(ValidationError.Required, AuthValidator.validarContrasenaLogin(""))
        assertNull(AuthValidator.validarContrasenaLogin("x"))
    }
}