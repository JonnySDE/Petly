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

class LoginViewModelTest {
    @get:Rule
    val mainRule = MainDispatcherRule()

    private val repositorio = FakeAuthRepository()
    private val viewModel = LoginViewModel(repositorio)
    private val estado get() = viewModel.uiState.value

    private fun escribirDatosValidos() {
        viewModel.onCorreoChange("ana@correo.com")
        viewModel.onContrasenaChange("miClave123")
    }

    @Test
    fun camposVaciosMuestranErroresYNoLlamanALaApi() {
        viewModel.onLoginClick()

        assertEquals(R.string.error_required, estado.correoError)
        assertEquals(R.string.error_required, estado.contrasenaError)
        assertEquals(0, repositorio.llamadasLogin)
        assertFalse(estado.isLoading)
    }

    @Test
    fun correoInvalidoMuestraErrorSoloEnElCorreo() {
        viewModel.onCorreoChange("ana")
        viewModel.onContrasenaChange("x")

        viewModel.onLoginClick()

        assertEquals(R.string.error_invalid_email, estado.correoError)
        assertNull(estado.contrasenaError)
        assertEquals(0, repositorio.llamadasLogin)
    }

    @Test
    fun loginExitosoMarcaSesionIniciadaYLimpiaLaContrasena() {
        escribirDatosValidos()

        viewModel.onLoginClick()

        assertTrue(estado.sesionIniciada)
        assertFalse(estado.isLoading)
        assertEquals("", estado.contrasena)
        assertEquals(1, repositorio.llamadasLogin)
        assertEquals("ana@correo.com" to "miClave123", repositorio.ultimoLogin)
    }

    @Test
    fun credencialesIncorrectasMuestranMensajeYConservanLosDatos() {
        repositorio.resultadoLogin = AppResult.Failure(DataError.InvalidCredentials)
        escribirDatosValidos()

        viewModel.onLoginClick()

        assertEquals(R.string.error_invalid_credentials, estado.mensaje)
        assertFalse(estado.sesionIniciada)
        assertFalse(estado.isLoading)
        assertEquals("miClave123", estado.contrasena)
    }

    @Test
    fun sinInternetMuestraSuMensaje() {
        repositorio.resultadoLogin = AppResult.Failure(DataError.NoInternet)
        escribirDatosValidos()

        viewModel.onLoginClick()

        assertEquals(R.string.error_no_internet, estado.mensaje)
    }

    @Test
    fun escribirEnUnCampoLimpiaSoloSuError() {
        viewModel.onLoginClick()

        viewModel.onCorreoChange("a")

        assertNull(estado.correoError)
        assertEquals(R.string.error_required, estado.contrasenaError)
    }

    @Test
    fun elMensajeSeLimpiaAlMostrarse() {
        repositorio.resultadoLogin = AppResult.Failure(DataError.Timeout)
        escribirDatosValidos()
        viewModel.onLoginClick()

        viewModel.onMensajeMostrado()

        assertNull(estado.mensaje)
    }

    @Test
    fun laNavegacionSeConsumeUnaSolaVez() {
        escribirDatosValidos()
        viewModel.onLoginClick()
        assertTrue(estado.sesionIniciada)

        viewModel.onNavegacionConsumida()

        assertFalse(estado.sesionIniciada)
    }
}