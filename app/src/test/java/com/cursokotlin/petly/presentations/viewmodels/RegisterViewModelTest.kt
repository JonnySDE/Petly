package com.cursokotlin.petly.presentations.viewmodels

import com.cursokotlin.petly.R
import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.model.DataError
import com.cursokotlin.petly.fakes.FakeAuthRepository
import com.cursokotlin.petly.fakes.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RegisterViewModelTest {
    @get:Rule
    val mainRule = MainDispatcherRule()

    private val repositorio = FakeAuthRepository()
    private val viewModel = RegisterViewModel(repositorio)
    private val estado get() = viewModel.uiState.value

    private fun escribirDatosValidos() {
        viewModel.onNombreChange("Ana López")
        viewModel.onCorreoChange("ana@correo.com")
        viewModel.onContrasenaChange("miClave123")
        viewModel.onConfirmacionChange("miClave123")
    }

    @Test
    fun camposVaciosMuestranTodosLosErrores() {
        viewModel.onRegisterClick()

        assertEquals(R.string.error_required, estado.nombreError)
        assertEquals(R.string.error_required, estado.correoError)
        assertEquals(R.string.error_required, estado.contrasenaError)
        assertEquals(R.string.error_required, estado.confirmacionError)
        assertEquals(0, repositorio.llamadasRegistro)
    }

    @Test
    fun contrasenaCortaMarcaError() {
        escribirDatosValidos()
        viewModel.onContrasenaChange("1234567")
        viewModel.onConfirmacionChange("1234567")

        viewModel.onRegisterClick()

        assertEquals(R.string.error_password_too_short, estado.contrasenaError)
        assertEquals(0, repositorio.llamadasRegistro)
    }

    @Test
    fun contrasenasDistintasMarcanLaConfirmacion() {
        escribirDatosValidos()
        viewModel.onConfirmacionChange("otraClave123")

        viewModel.onRegisterClick()

        assertEquals(R.string.error_password_mismatch, estado.confirmacionError)
        assertNull(estado.contrasenaError)
        assertEquals(0, repositorio.llamadasRegistro)
    }

    @Test
    fun cambiarLaContrasenaLimpiaElErrorDeConfirmacion() {
        escribirDatosValidos()
        viewModel.onConfirmacionChange("otraClave123")
        viewModel.onRegisterClick()

        viewModel.onContrasenaChange("nuevaClave123")

        assertNull(estado.confirmacionError)
    }

    @Test
    fun registroExitosoMarcaExitoYLimpiaElFormulario() {
        escribirDatosValidos()

        viewModel.onRegisterClick()

        assertTrue(estado.registroExitoso)
        assertFalse(estado.isLoading)
        assertEquals("", estado.contrasena)
        assertEquals("", estado.confirmacion)
        assertEquals(Triple("Ana López", "ana@correo.com", "miClave123"), repositorio.ultimoRegistro)
    }

    @Test
    fun correoRepetidoSeMuestraJuntoAlCampoYNoEnElSnackbar() {
        repositorio.resultadoRegistro = AppResult.Failure(DataError.EmailTaken)
        escribirDatosValidos()

        viewModel.onRegisterClick()

        assertEquals(R.string.error_email_taken, estado.correoError)
        assertNull(estado.mensaje)
        assertFalse(estado.registroExitoso)
        assertFalse(estado.isLoading)
    }

    @Test
    fun sinInternetMuestraSuMensajeYConservaLosDatos() {
        repositorio.resultadoRegistro = AppResult.Failure(DataError.NoInternet)
        escribirDatosValidos()

        viewModel.onRegisterClick()

        assertEquals(R.string.error_no_internet, estado.mensaje)
        assertEquals("Ana López", estado.nombre)
        assertFalse(estado.registroExitoso)
    }
}