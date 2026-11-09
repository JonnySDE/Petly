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

class HomeViewModelTest {
    @get:Rule
    val mainRule = MainDispatcherRule()

    private val auth = FakeAuthRepository().apply { nombre = "Jonathan Rodriguez" }
    private val mascotas = FakeMascotaRepository()
    private val reloj = Clock.fixed(Instant.parse("2026-11-05T20:00:00Z"), ZoneId.of("America/Mexico_City"))

    private val luna = Mascota("m1", "Luna", "Gato", 3, true, null, null)
    private val bob = Mascota(
        "m2", "Bob", "Perro", 4, true, null,
        UltimaAlimentacion("Croquetas", "250 g", Instant.parse("2026-11-05T15:00:00Z")),
    )

    private fun crearViewModel() = HomeViewModel(auth, mascotas, reloj)

    @Test
    fun alAbrirMuestraElSaludoConNombreEIniciales() {
        val estado = crearViewModel().uiState.value

        assertEquals("Jonathan Rodriguez", estado.nombreUsuario)
        assertEquals("JR", estado.iniciales)
    }

    @Test
    fun sinNombreGuardadoElSaludoQuedaVacio() {
        auth.nombre = null

        val estado = crearViewModel().uiState.value

        assertEquals("", estado.nombreUsuario)
        assertEquals("", estado.iniciales)
    }

    @Test
    fun alAbrirCargaLasMascotas() {
        mascotas.resultadoLista = AppResult.Success(listOf(luna, bob))

        val estado = crearViewModel().uiState.value

        assertFalse(estado.isLoading)
        assertEquals(listOf("Luna", "Bob"), estado.mascotas.map { it.nombre })
        assertFalse(estado.mascotas[0].yaComio)
        assertTrue(estado.mascotas[1].yaComio)
        assertNull(estado.errorMessage)
    }

    @Test
    fun sinMascotasQuedaVacioSinError() {
        val estado = crearViewModel().uiState.value

        assertFalse(estado.isLoading)
        assertTrue(estado.mascotas.isEmpty())
        assertNull(estado.errorMessage)
    }

    @Test
    fun errorSinDatosMuestraErrorConReintento() {
        mascotas.resultadoLista = AppResult.Failure(DataError.NoInternet)

        val estado = crearViewModel().uiState.value

        assertEquals(R.string.error_no_internet, estado.errorMessage)
        assertTrue(estado.mascotas.isEmpty())
        assertFalse(estado.isLoading)
    }

    @Test
    fun reintentarRecargaYQuitaElError() {
        mascotas.resultadoLista = AppResult.Failure(DataError.Timeout)
        val viewModel = crearViewModel()
        mascotas.resultadoLista = AppResult.Success(listOf(luna))

        viewModel.onReintentarClick()

        val estado = viewModel.uiState.value
        assertNull(estado.errorMessage)
        assertEquals(1, estado.mascotas.size)
        assertFalse(estado.isLoading)
        assertEquals(2, mascotas.llamadasLista)
    }

    @Test
    fun errorConDatosYaCargadosConservaLaListaYMuestraMensaje() {
        mascotas.resultadoLista = AppResult.Success(listOf(luna))
        val viewModel = crearViewModel()
        mascotas.resultadoLista = AppResult.Failure(DataError.NoInternet)

        viewModel.onReintentarClick()

        val estado = viewModel.uiState.value
        assertEquals(1, estado.mascotas.size)
        assertEquals(R.string.error_no_internet, estado.mensaje)
        assertNull(estado.errorMessage)
    }

    @Test
    fun tokenExpiradoCierraLaSesionYNavegaAlLogin() {
        mascotas.resultadoLista = AppResult.Failure(DataError.Unauthorized)

        val estado = crearViewModel().uiState.value

        assertEquals(1, auth.llamadasCerrarSesion)
        assertTrue(estado.sesionCerrada)
        assertFalse(estado.isLoading)
    }

    @Test
    fun elMensajeSeLimpiaAlMostrarse() {
        mascotas.resultadoLista = AppResult.Success(listOf(luna))
        val viewModel = crearViewModel()
        mascotas.resultadoLista = AppResult.Failure(DataError.Timeout)
        viewModel.onReintentarClick()

        viewModel.onMensajeMostrado()

        assertNull(viewModel.uiState.value.mensaje)
    }

    @Test
    fun cerrarSesionBorraLaSesionYAvisaParaNavegar() {
        val viewModel = crearViewModel()

        viewModel.onCerrarSesionClick()

        assertEquals(1, auth.llamadasCerrarSesion)
        assertTrue(viewModel.uiState.value.sesionCerrada)
    }

    @Test
    fun laNavegacionSeConsumeUnaSolaVez() {
        val viewModel = crearViewModel()
        viewModel.onCerrarSesionClick()

        viewModel.onNavegacionConsumida()

        assertFalse(viewModel.uiState.value.sesionCerrada)
    }

    @Test
    fun actualizarVuelveAPedirLasMascotas() {
        mascotas.resultadoLista = AppResult.Success(listOf(luna))
        val viewModel = crearViewModel()
        mascotas.resultadoLista = AppResult.Success(listOf(luna, bob))

        viewModel.onActualizar()

        assertEquals(2, viewModel.uiState.value.mascotas.size)
        assertEquals(2, mascotas.llamadasLista)
    }
}