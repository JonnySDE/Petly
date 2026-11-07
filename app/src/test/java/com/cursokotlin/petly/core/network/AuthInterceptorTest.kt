package com.cursokotlin.petly.core.network

import com.cursokotlin.petly.fakes.FakeSessionStorage
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AuthInterceptorTest {
    private val servidor = MockWebServer()
    private val sesion = FakeSessionStorage()
    private val cliente = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(sesion))
        .build()

    @Before
    fun iniciar() {
        servidor.start()
    }

    @After
    fun detener() {
        servidor.shutdown()
    }

    /** Hace una petición y devuelve el encabezado Authorization que recibió el servidor. */
    private fun llamar(): String? {
        servidor.enqueue(MockResponse().setResponseCode(200))
        val peticion = Request.Builder().url(servidor.url("/mascotas")).build()
        cliente.newCall(peticion).execute().close()
        return servidor.takeRequest().getHeader("Authorization")
    }

    @Test
    fun conSesionAgregaElEncabezadoBearer() {
        sesion.guardarSesion("abc123", "Ana")
        assertEquals("Bearer abc123", llamar())
    }

    @Test
    fun sinSesionNoAgregaEncabezado() {
        assertNull(llamar())
    }

    @Test
    fun trasCerrarSesionDejaDeEnviarElToken() {
        sesion.guardarSesion("abc123", "Ana")
        assertEquals("Bearer abc123", llamar())
        sesion.borrarSesion()
        assertNull(llamar())
    }
}