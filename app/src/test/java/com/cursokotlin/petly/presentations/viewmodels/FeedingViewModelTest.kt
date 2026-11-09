package com.cursokotlin.petly.presentations.viewmodels

import com.cursokotlin.petly.R
import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.model.DataError
import com.cursokotlin.petly.domain.model.Mascota
import com.cursokotlin.petly.domain.model.UltimaAlimentacion
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

class FeedingViewModelTest {
    @get:Rule
    val mainRule = MainDispatcherRule()

    private val auth = FakeAuthRepository()
    private val mascotas = FakeMascotaRepository()
    private val ahora = Instant.parse("2026-11-05T20:00:00Z")
    private val reloj = Clock.fixed(ahora, ZoneId.of("America/Mexico_City"))

    private val luna = Mascota("m1", "Luna", "Gato", 3, true, null, null)
    private val bob = Mascota(
        "m2", "Bob", "Perro", 4, true, null,
        UltimaAlimentacion("Croquetas", "250 g", Instant.parse("2026-11-04T15:00:00Z")),
    )

    private fun crearViewModel(id: String = "m1") = FeedingViewModel(id, mascotas, auth, reloj)

    private fun llenar(viewModel: FeedingViewModel, tipo: String = "Croquetas", cantidad: String = "200 g") {
        viewModel.onTipoChange(tipo)
        viewModel.onCantidadChange(cantidad)
    }

    @Test
    fun alAbrirCargaNombreYEspecie() {
        mascotas.resultadoDetalle = AppResult.Success(luna)
        val estado = crearViewModel("m1").uiState.value
        assertEquals("m1", mascotas.ultimoIdConsultado)
        assertEquals("Luna", estado.nombreMascota)
        assertEquals("Gato", estado.especie)
        assertFalse(estado.isLoadingInicial)
        assertNull(estado.errorCarga)
    }

    @Test
    fun precargaTipoYCantidadDeLaUltimaComida() {
        mascotas.resultadoDetalle = AppResult.Success(bob)
        val estado = crearViewModel("m2").uiState.value
        assertEquals("Croquetas", estado.tipoAlimento)
        assertEquals("250 g", estado.cantidad)
    }

    @Test
    fun sinComidaPreviaLosCamposQuedanVacios() {
        mascotas.resultadoDetalle = AppResult.Success(luna)
        val estado = crearViewModel().uiState.value
        assertEquals("", estado.tipoAlimento)
        assertEquals("", estado.cantidad)
    }

    @Test
    fun camposVaciosMuestranErroresYNoLlamanALaApi() {
        mascotas.resultadoDetalle = AppResult.Success(luna)
        val viewModel = crearViewModel()
        viewModel.onGuardarClick()
        val estado = viewModel.uiState.value
        assertEquals(R.string.error_required, estado.tipoError)
        assertEquals(R.string.error_required, estado.cantidadError)
        assertEquals(0, mascotas.llamadasAlimentacion)
    }

    @Test
    fun tipoYCantidadDemasiadoLargosMuestranError() {
        mascotas.resultadoDetalle = AppResult.Success(luna)
        val viewModel = crearViewModel()
        llenar(viewModel, "a".repeat(51), "b".repeat(31))
        viewModel.onGuardarClick()
        val estado = viewModel.uiState.value
        assertEquals(R.string.error_too_long, estado.tipoError)
        assertEquals(R.string.error_too_long, estado.cantidadError)
        assertEquals(0, mascotas.llamadasAlimentacion)
    }

    @Test
    fun guardarEnviaDatosRecortadosYLaHoraDelReloj() {
        mascotas.resultadoDetalle = AppResult.Success(luna)
        mascotas.resultadoAlimentacion = AppResult.Success(luna)
        val viewModel = crearViewModel()
        llenar(viewModel, "  Croquetas ", " 200 g  ")
        viewModel.onGuardarClick()
        val (id, alimentacion) = mascotas.ultimaAlimentacionRegistrada!!
        assertEquals("m1", id)
        assertEquals("Croquetas", alimentacion.tipoAlimento)
        assertEquals("200 g", alimentacion.cantidad)
        assertEquals(ahora, alimentacion.fechaHora)
    }

    @Test
    fun guardarConExitoAvisaQueSeRegistro() {
        mascotas.resultadoDetalle = AppResult.Success(luna)
        mascotas.resultadoAlimentacion = AppResult.Success(luna)
        val viewModel = crearViewModel()
        llenar(viewModel)
        viewModel.onGuardarClick()
        assertEquals(R.string.feeding_registered, viewModel.uiState.value.guardadoMensaje)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun guardadoConsumidoLimpiaElEvento() {
        mascotas.resultadoDetalle = AppResult.Success(luna)
        mascotas.resultadoAlimentacion = AppResult.Success(luna)
        val viewModel = crearViewModel()
        llenar(viewModel)
        viewModel.onGuardarClick()
        viewModel.onGuardadoConsumido()
        assertNull(viewModel.uiState.value.guardadoMensaje)
    }

    @Test
    fun errorDeRedAlGuardarMuestraMensajeYConservaLosDatos() {
        mascotas.resultadoDetalle = AppResult.Success(luna)
        mascotas.resultadoAlimentacion = AppResult.Failure(DataError.NoInternet)
        val viewModel = crearViewModel()
        llenar(viewModel)
        viewModel.onGuardarClick()
        val estado = viewModel.uiState.value
        assertEquals(R.string.error_no_internet, estado.mensaje)
        assertEquals("Croquetas", estado.tipoAlimento)
        assertEquals("200 g", estado.cantidad)
        assertNull(estado.errorCarga)
        assertFalse(estado.isSaving)
    }

    @Test
    fun noAutorizadoAlGuardarCierraLaSesion() {
        mascotas.resultadoDetalle = AppResult.Success(luna)
        mascotas.resultadoAlimentacion = AppResult.Failure(DataError.Unauthorized)
        val viewModel = crearViewModel()
        llenar(viewModel)
        viewModel.onGuardarClick()
        assertTrue(viewModel.uiState.value.sesionCerrada)
    }

    @Test
    fun errorAlCargarMuestraErrorYReintentarLoRecupera() {
        mascotas.resultadoDetalle = AppResult.Failure(DataError.Timeout)
        val viewModel = crearViewModel()
        assertEquals(R.string.error_timeout, viewModel.uiState.value.errorCarga)
        mascotas.resultadoDetalle = AppResult.Success(luna)
        viewModel.onReintentarCargaClick()
        assertNull(viewModel.uiState.value.errorCarga)
        assertEquals("Luna", viewModel.uiState.value.nombreMascota)
        assertEquals(2, mascotas.llamadasDetalle)
    }

    @Test
    fun escribirLimpiaSoloElErrorDeSuCampo() {
        mascotas.resultadoDetalle = AppResult.Success(luna)
        val viewModel = crearViewModel()
        viewModel.onGuardarClick()
        viewModel.onTipoChange("Croquetas")
        assertNull(viewModel.uiState.value.tipoError)
        assertEquals(R.string.error_required, viewModel.uiState.value.cantidadError)
    }
}