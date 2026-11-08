package com.cursokotlin.petly.presentations.viewmodels

import com.cursokotlin.petly.fakes.FakeAuthRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeViewModelTest {
    private val repositorio = FakeAuthRepository().apply { nombre = "Ana López" }
    private val viewModel = HomeViewModel(repositorio)
    private val estado get() = viewModel.uiState.value

    @Test
    fun muestraElNombreDelUsuarioConSesion() {
        assertEquals("Ana López", estado.nombreUsuario)
    }

    @Test
    fun sinNombreGuardadoElSaludoQuedaVacio() {
        repositorio.nombre = null

        assertEquals("", HomeViewModel(repositorio).uiState.value.nombreUsuario)
    }

    @Test
    fun cerrarSesionBorraLaSesionYAvisaParaNavegar() {
        viewModel.onCerrarSesionClick()

        assertEquals(1, repositorio.llamadasCerrarSesion)
        assertTrue(estado.sesionCerrada)
    }

    @Test
    fun laNavegacionSeConsumeUnaSolaVez() {
        viewModel.onCerrarSesionClick()

        viewModel.onNavegacionConsumida()

        assertFalse(estado.sesionCerrada)
    }
}