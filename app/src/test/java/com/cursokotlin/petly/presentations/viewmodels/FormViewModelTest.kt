package com.cursokotlin.petly.presentations.viewmodels

import com.cursokotlin.petly.R
import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.model.DataError
import com.cursokotlin.petly.domain.model.Mascota
import com.cursokotlin.petly.domain.model.MascotaInput
import com.cursokotlin.petly.fakes.FakeAuthRepository
import com.cursokotlin.petly.fakes.FakeMascotaRepository
import com.cursokotlin.petly.fakes.MainDispatcherRule
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FormViewModelTest {
    @get:Rule
    val mainRule = MainDispatcherRule()

    private val auth = FakeAuthRepository()
    private val mascotas = FakeMascotaRepository()

    // Hoy es el 5 de noviembre de 2026 en Chiapas.
    private val reloj = Clock.fixed(Instant.parse("2026-11-05T20:00:00Z"), ZoneId.of("America/Mexico_City"))

    private val luna = Mascota("m1", "Luna", "Gato", 3, true, LocalDate.of(2026, 5, 12), null)

    private fun millis(fecha: LocalDate) = fecha.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    private fun crearViewModel(id: String? = null) = FormViewModel(id, mascotas, auth, reloj)

    private fun FormViewModel.escribirDatosValidos() {
        onNombreChange("  Luna ")
        onEspecieChange("Gato")
        onEdadChange("3")
        onVacunadoChange(true)
        onFechaVisitaSeleccionada(millis(LocalDate.of(2026, 5, 12)))
    }

    // ---------- Crear ----------
    @Test
    fun enModoCrearElFormularioEmpiezaVacioConPerroSeleccionado() {
        val estado = crearViewModel().uiState.value

        assertFalse(estado.modoEdicion)
        assertFalse(estado.isLoadingInicial)
        assertEquals("", estado.nombre)
        assertEquals("Perro", estado.especie)
        assertEquals("", estado.edad)
        assertFalse(estado.estaVacunado)
        assertNull(estado.fechaVisita)
        assertEquals(0, mascotas.llamadasDetalle)
    }

    @Test
    fun camposVaciosMuestranErroresYNoLlamanALaApi() {
        val viewModel = crearViewModel()

        viewModel.onGuardarClick()

        val estado = viewModel.uiState.value
        assertEquals(R.string.error_required, estado.nombreError)
        assertEquals(R.string.error_required, estado.edadError)
        assertNull(estado.especieError)
        assertNull(estado.fechaError)
        assertEquals(0, mascotas.llamadasCrear)
        assertFalse(estado.isSaving)
    }

    @Test
    fun edadFueraDeRangoMuestraError() {
        val viewModel = crearViewModel()
        viewModel.escribirDatosValidos()
        viewModel.onEdadChange("31")

        viewModel.onGuardarClick()

        assertEquals(R.string.error_age_range, viewModel.uiState.value.edadError)
        assertEquals(0, mascotas.llamadasCrear)
    }

    @Test
    fun edadQueNoEsNumeroMuestraError() {
        val viewModel = crearViewModel()
        viewModel.escribirDatosValidos()
        viewModel.onEdadChange("tres")

        viewModel.onGuardarClick()

        assertEquals(R.string.error_invalid_number, viewModel.uiState.value.edadError)
    }

    @Test
    fun fechaFuturaMuestraError() {
        val viewModel = crearViewModel()
        viewModel.escribirDatosValidos()
        viewModel.onFechaVisitaSeleccionada(millis(LocalDate.of(2026, 11, 6)))

        viewModel.onGuardarClick()

        assertEquals(R.string.error_future_date, viewModel.uiState.value.fechaError)
        assertEquals(0, mascotas.llamadasCrear)
    }

    @Test
    fun laFechaDeHoyEsValida() {
        mascotas.resultadoCrear = AppResult.Success(luna)
        val viewModel = crearViewModel()
        viewModel.escribirDatosValidos()
        viewModel.onFechaVisitaSeleccionada(millis(LocalDate.of(2026, 11, 5)))

        viewModel.onGuardarClick()

        assertNull(viewModel.uiState.value.fechaError)
        assertEquals(1, mascotas.llamadasCrear)
    }

    @Test
    fun guardarCreaLaMascotaConLosDatosDelFormulario() {
        mascotas.resultadoCrear = AppResult.Success(luna)
        val viewModel = crearViewModel()
        viewModel.escribirDatosValidos()

        viewModel.onGuardarClick()

        assertEquals(
            MascotaInput("Luna", "Gato", 3, true, LocalDate.of(2026, 5, 12)),
            mascotas.ultimoCreado,
        )
        val estado = viewModel.uiState.value
        assertEquals(R.string.pet_created, estado.guardadoMensaje)
        assertFalse(estado.isSaving)
        assertEquals(0, mascotas.llamadasEditar)
    }

    @Test
    fun guardarSinFechaDeVisitaEnviaNulo() {
        mascotas.resultadoCrear = AppResult.Success(luna)
        val viewModel = crearViewModel()
        viewModel.escribirDatosValidos()
        viewModel.onQuitarFechaClick()

        viewModel.onGuardarClick()

        assertNull(mascotas.ultimoCreado?.fechaUltimaVisita)
    }

    @Test
    fun unErrorDelServidorMuestraMensajeYConservaLosDatos() {
        mascotas.resultadoCrear = AppResult.Failure(DataError.NoInternet)
        val viewModel = crearViewModel()
        viewModel.escribirDatosValidos()

        viewModel.onGuardarClick()

        val estado = viewModel.uiState.value
        assertEquals(R.string.error_no_internet, estado.mensaje)
        assertNull(estado.guardadoMensaje)
        assertFalse(estado.isSaving)
        assertEquals("  Luna ", estado.nombre)
    }

    @Test
    fun escribirEnUnCampoLimpiaSoloSuError() {
        val viewModel = crearViewModel()
        viewModel.onGuardarClick()

        viewModel.onNombreChange("L")

        val estado = viewModel.uiState.value
        assertNull(estado.nombreError)
        assertEquals(R.string.error_required, estado.edadError)
    }

    @Test
    fun laFechaVieneDeLosMilisegundosDelSelectorYSePuedeQuitar() {
        val viewModel = crearViewModel()

        viewModel.onFechaVisitaSeleccionada(millis(LocalDate.of(2026, 5, 12)))

        val estado = viewModel.uiState.value
        assertEquals(LocalDate.of(2026, 5, 12), estado.fechaVisita)
        assertEquals("2026-05-12", estado.fechaVisitaTexto)
        assertEquals(millis(LocalDate.of(2026, 5, 12)), estado.fechaVisitaMillis)

        viewModel.onQuitarFechaClick()

        assertNull(viewModel.uiState.value.fechaVisita)
        assertEquals("", viewModel.uiState.value.fechaVisitaTexto)
        assertNull(viewModel.uiState.value.fechaVisitaMillis)
    }

    @Test
    fun elMensajeYElGuardadoSeConsumenUnaSolaVez() {
        mascotas.resultadoCrear = AppResult.Success(luna)
        val viewModel = crearViewModel()
        viewModel.escribirDatosValidos()
        viewModel.onGuardarClick()

        viewModel.onGuardadoConsumido()

        assertNull(viewModel.uiState.value.guardadoMensaje)
    }

    // ---------- Editar ----------
    @Test
    fun enModoEdicionPrecargaLosDatosActuales() {
        mascotas.resultadoDetalle = AppResult.Success(luna)

        val estado = crearViewModel("m1").uiState.value

        assertEquals("m1", mascotas.ultimoIdConsultado)
        assertTrue(estado.modoEdicion)
        assertFalse(estado.isLoadingInicial)
        assertEquals("Luna", estado.nombre)
        assertEquals("Gato", estado.especie)
        assertEquals("3", estado.edad)
        assertTrue(estado.estaVacunado)
        assertEquals(LocalDate.of(2026, 5, 12), estado.fechaVisita)
    }

    @Test
    fun unaEspecieDesconocidaSeAgregaALaLista() {
        mascotas.resultadoDetalle = AppResult.Success(luna.copy(especie = "Conejo"))

        val estado = crearViewModel("m1").uiState.value

        assertEquals("Conejo", estado.especie)
        assertTrue("Conejo" in estado.especies)
        assertTrue("Perro" in estado.especies)
    }

    @Test
    fun guardarEdicionLlamaAEditarConElId() {
        mascotas.resultadoDetalle = AppResult.Success(luna)
        mascotas.resultadoEditar = AppResult.Success(luna)
        val viewModel = crearViewModel("m1")
        viewModel.onNombreChange("Luna II")

        viewModel.onGuardarClick()

        assertEquals(
            "m1" to MascotaInput("Luna II", "Gato", 3, true, LocalDate.of(2026, 5, 12)),
            mascotas.ultimaEdicion,
        )
        assertEquals(R.string.pet_updated, viewModel.uiState.value.guardadoMensaje)
        assertEquals(0, mascotas.llamadasCrear)
    }

    @Test
    fun errorAlCargarMuestraErrorYReintentarLoResuelve() {
        mascotas.resultadoDetalle = AppResult.Failure(DataError.Timeout)
        val viewModel = crearViewModel("m1")
        assertEquals(R.string.error_timeout, viewModel.uiState.value.errorCarga)

        mascotas.resultadoDetalle = AppResult.Success(luna)
        viewModel.onReintentarCargaClick()

        val estado = viewModel.uiState.value
        assertNull(estado.errorCarga)
        assertEquals("Luna", estado.nombre)
    }

    @Test
    fun tokenExpiradoAlGuardarCierraLaSesion() {
        mascotas.resultadoDetalle = AppResult.Success(luna)
        mascotas.resultadoEditar = AppResult.Failure(DataError.Unauthorized)
        val viewModel = crearViewModel("m1")

        viewModel.onGuardarClick()

        assertEquals(1, auth.llamadasCerrarSesion)
        assertTrue(viewModel.uiState.value.sesionCerrada)
        assertFalse(viewModel.uiState.value.isSaving)
    }
}