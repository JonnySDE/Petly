package com.cursokotlin.petly.core.network

import com.cursokotlin.petly.domain.model.DataError
import com.cursokotlin.petly.domain.model.FieldError
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.time.format.DateTimeParseException

class SafeApiCallTest {

    private fun http(codigo: Int, cuerpo: String) = HttpException(
        Response.error<Any>(codigo, cuerpo.toResponseBody("application/json".toMediaType())),
    )

    @Test
    fun sinRedEsNoInternet() {
        assertEquals(DataError.NoInternet, UnknownHostException("sin red").toDataError())
        assertEquals(DataError.NoInternet, IOException("fallo").toDataError())
    }

    @Test
    fun timeoutNoSeConfundeConFaltaDeRed() {
        assertEquals(DataError.Timeout, SocketTimeoutException("lento").toDataError())
    }

    @Test
    fun credencialesIncorrectasYTokenInvalidoSonErroresDistintos() {
        val credenciales = http(401, """{"codigo":"CREDENCIALES_INVALIDAS","mensaje":"Correo o contraseña incorrectos"}""")
        val token = http(401, """{"codigo":"TOKEN_INVALIDO","mensaje":"Sesión inválida o expirada"}""")
        assertEquals(DataError.InvalidCredentials, credenciales.toDataError())
        assertEquals(DataError.Unauthorized, token.toDataError())
    }

    @Test
    fun correoDuplicadoYNoEncontrado() {
        assertEquals(
            DataError.EmailTaken,
            http(409, """{"codigo":"CORREO_DUPLICADO","mensaje":"El correo ya está registrado"}""").toDataError(),
        )
        assertEquals(
            DataError.NotFound,
            http(404, """{"codigo":"NO_ENCONTRADO","mensaje":"Mascota no encontrada"}""").toDataError(),
        )
    }

    @Test
    fun validacionTraeLosCamposConError() {
        val cuerpo = """{"codigo":"VALIDACION","mensaje":"Hay datos inválidos","campos":[{"campo":"edadAnios","mensaje":"Debe ser menor o igual a 30"}]}"""
        assertEquals(
            DataError.Validation(listOf(FieldError("edadAnios", "Debe ser menor o igual a 30"))),
            http(400, cuerpo).toDataError(),
        )
    }

    @Test
    fun errorDelServidorConservaElCodigoHttp() {
        assertEquals(
            DataError.Http(500),
            http(500, """{"codigo":"ERROR_INTERNO","mensaje":"Ocurrió un error en el servidor"}""").toDataError(),
        )
    }

    @Test
    fun siElCuerpoNoEsJsonDecidePorElCodigoHttp() {
        assertEquals(DataError.Http(502), http(502, "<html>Bad Gateway</html>").toDataError())
        assertEquals(DataError.Unauthorized, http(401, "").toDataError())
        assertEquals(DataError.NotFound, http(404, "no existe").toDataError())
    }

    @Test
    fun respuestaMalFormadaEsSerialization() {
        assertEquals(DataError.Serialization, SerializationException("json roto").toDataError())
    }

    @Test
    fun cualquierOtraExcepcionEsUnknown() {
        assertEquals(DataError.Unknown, IllegalStateException("inesperado").toDataError())
    }
    @Test
    fun unaFechaMalFormadaEsSerialization() {
        assertEquals(DataError.Serialization, DateTimeParseException("mala", "ayer", 0).toDataError())
    }
}