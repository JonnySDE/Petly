package com.cursokotlin.petly.presentations.viewmodels

import com.cursokotlin.petly.R
import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.model.DataError
import com.cursokotlin.petly.domain.model.Mascota
import com.cursokotlin.petly.fakes.FakeAuthRepository
import com.cursokotlin.petly.fakes.FakeMascotaRepository
import com.cursokotlin.petly.fakes.MainDispatcherRule
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetailViewModelTest {
    @get:Rule
    val mainRule = MainDispatcherRule()

    private val auth = FakeAuthRepository()
    private val mascotas = FakeMascotaRepository()
    private val reloj = Clock.fixed(Instant.parse("2026-11-05T20:00:00Z"), ZoneId.of("America/Mexico_City"))
    private val luna = Mascota("m1", "Luna", "Gato", 3, true, null, null)

    // Carga al crearse: se construye dentro de cada prueba, con la regla del hilo principal ya activa.
    private fun crearViewModel(id: String = "m1") = DetailViewModel(id, mascotas, auth, reloj)

    @Test
    fun alAbrirCargaLaMascotaConSuId() {
        mascotas.resultadoDetalle = AppResult.Success(luna)

        val estado = crearViewModel("m1").uiState.value

        assertEquals("m1", mascotas.ultimoIdConsultado)
        assertEquals("Luna", estado.mascota?.nombre)
        assertFalse(estado.isLoading)
        assertNull(estado.errorMessage)
    }

    @Test
    fun mascotaInexistenteMuestraErrorDeNoEncontrada() {
        // Por defecto el repositorio de pruebas responde NotFound.
        val estado = crearViewModel("nada").uiState.value

        assertEquals(R.string.error_not_found, estado.errorMessage)
        assertNull(estado.mascota)
        assertFalse(estado.isLoading)
    }

    @Test
    fun errorSinFichaMuestraErrorConReintento() {
        mascotas.resultadoDetalle = AppResult.Failure(DataError.NoInternet)

        val estado = crearViewModel().uiState.value

        assertEquals(R.string.error_no_internet, estado.errorMessage)
        assertNull(estado.mascota)
    }

    @Test
    fun reintentarRecargaYQuitaElError() {
        mascotas.resultadoDetalle = AppResult.Failure(DataError.Timeout)
        val viewModel = crearViewModel()
        mascotas.resultadoDetalle = AppResult.Success(luna)

        viewModel.onReintentarClick()

        val estado = viewModel.uiState.value
        assertNull(estado.errorMessage)
        assertEquals("Luna", estado.mascota?.nombre)
        assertEquals(2, mascotas.llamadasDetalle)
    }

    @Test
    fun errorConFichaYaCargadaLaConservaYMuestraMensaje() {
        mascotas.resultadoDetalle = AppResult.Success(luna)
        val viewModel = crearViewModel()
        mascotas.resultadoDetalle = AppResult.Failure(DataError.NoInternet)

        viewModel.onReintentarClick()

        val estado = viewModel.uiState.value
        assertEquals("Luna", estado.mascota?.nombre)
        assertEquals(R.string.error_no_internet, estado.mensaje)
        assertNull(estado.errorMessage)
    }

    @Test
    fun tokenExpiradoCierraLaSesionYNavegaAlLogin() {
        mascotas.resultadoDetalle = AppResult.Failure(DataError.Unauthorized)

        val estado = crearViewModel().uiState.value

        assertEquals(1, auth.llamadasCerrarSesion)
        assertTrue(estado.sesionCerrada)
        assertFalse(estado.isLoading)
    }

    @Test
    fun elMensajeSeLimpiaAlMostrarse() {
        mascotas.resultadoDetalle = AppResult.Success(luna)
        val viewModel = crearViewModel()
        mascotas.resultadoDetalle = AppResult.Failure(DataError.Timeout)
        viewModel.onReintentarClick()

        viewModel.onMensajeMostrado()

        assertNull(viewModel.uiState.value.mensaje)
    }

    @Test
    fun laNavegacionSeConsumeUnaSolaVez() {
        mascotas.resultadoDetalle = AppResult.Failure(DataError.Unauthorized)
        val viewModel = crearViewModel()
        assertTrue(viewModel.uiState.value.sesionCerrada)

        viewModel.onNavegacionConsumida()

        assertFalse(viewModel.uiState.value.sesionCerrada)
    }

    @Test
    fun actualizarVuelveAPedirLaMascota() {
        mascotas.resultadoDetalle = AppResult.Success(luna)
        val viewModel = crearViewModel()
        mascotas.resultadoDetalle = AppResult.Success(luna.copy(nombre = "Luna II"))

        viewModel.onActualizar()

        assertEquals("Luna II", viewModel.uiState.value.mascota?.nombre)
        assertEquals(2, mascotas.llamadasDetalle)
    }
}