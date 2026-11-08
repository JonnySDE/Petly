package com.cursokotlin.petly.data.repository

import com.cursokotlin.petly.core.network.AuthInterceptor
import com.cursokotlin.petly.data.datasource.MascotaApi
import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.model.DataError
import com.cursokotlin.petly.domain.model.FieldError
import com.cursokotlin.petly.domain.model.Mascota
import com.cursokotlin.petly.domain.model.MascotaInput
import com.cursokotlin.petly.domain.model.UltimaAlimentacion
import com.cursokotlin.petly.fakes.FakeSessionStorage
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

@OptIn(ExperimentalSerializationApi::class)
class MascotaRepositoryImplTest {
    private val servidor = MockWebServer()
    private val sesion = FakeSessionStorage().apply { guardarSesion("token-abc", "Ana") }
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
    private lateinit var repositorio: MascotaRepositoryImpl

    private val lunaJson =
        """{"id":"m1","nombre":"Luna","especie":"Gato","edadAnios":3,"estaVacunado":true,"fechaUltimaVisita":"2020-01-01","tipoAlimento":null,"cantidadAlimento":null,"fechaHoraUltimaAlimentacion":null}"""
    private val luna = Mascota("m1", "Luna", "Gato", 3, true, LocalDate.of(2020, 1, 1), null)

    private val bobJson =
        """{"id":"m2","nombre":"Bob","especie":"Perro","edadAnios":4,"estaVacunado":false,"fechaUltimaVisita":null,"tipoAlimento":"Croquetas","cantidadAlimento":"250 g","fechaHoraUltimaAlimentacion":"2026-11-05T14:30:00Z"}"""
    private val bob = Mascota(
        "m2", "Bob", "Perro", 4, false, null,
        UltimaAlimentacion("Croquetas", "250 g", Instant.parse("2026-11-05T14:30:00Z")),
    )

    private fun crearRepositorio(baseUrl: String): MascotaRepositoryImpl {
        val cliente = OkHttpClient.Builder().addInterceptor(AuthInterceptor(sesion)).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(cliente)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        return MascotaRepositoryImpl(retrofit.create<MascotaApi>())
    }

    @Before
    fun iniciar() {
        servidor.start()
        repositorio = crearRepositorio(servidor.url("/").toString())
    }

    @After
    fun detener() {
        servidor.shutdown()
    }

    @Test
    fun obtenerMascotasConvierteLaListaADominio() = runBlocking {
        servidor.enqueue(MockResponse().setResponseCode(200).setBody("[$lunaJson,$bobJson]"))

        val resultado = repositorio.obtenerMascotas()

        assertEquals(AppResult.Success(listOf(luna, bob)), resultado)
        val peticion = servidor.takeRequest()
        assertEquals("GET", peticion.method)
        assertEquals("/mascotas", peticion.path)
    }

    @Test
    fun obtenerMascotasConListaVaciaDevuelveListaVacia() = runBlocking {
        servidor.enqueue(MockResponse().setResponseCode(200).setBody("[]"))

        assertEquals(AppResult.Success(emptyList<Mascota>()), repositorio.obtenerMascotas())
    }

    @Test
    fun obtenerMascotaUsaLaRutaConElId() = runBlocking {
        servidor.enqueue(MockResponse().setResponseCode(200).setBody(bobJson))

        val resultado = repositorio.obtenerMascota("m2")

        assertEquals(AppResult.Success(bob), resultado)
        assertEquals("/mascotas/m2", servidor.takeRequest().path)
    }

    @Test
    fun crearMascotaEnviaElJsonEsperadoYDevuelveLaCreada() = runBlocking {
        servidor.enqueue(MockResponse().setResponseCode(201).setBody(lunaJson))

        val resultado = repositorio.crearMascota(
            MascotaInput(" Luna ", "Gato", 3, true, LocalDate.of(2020, 1, 1)),
        )

        assertEquals(AppResult.Success(luna), resultado)
        val peticion = servidor.takeRequest()
        assertEquals("POST", peticion.method)
        assertEquals("/mascotas", peticion.path)
        assertEquals(
            """{"nombre":"Luna","especie":"Gato","edadAnios":3,"estaVacunado":true,"fechaUltimaVisita":"2020-01-01"}""",
            peticion.body.readUtf8(),
        )
    }

    @Test
    fun editarMascotaUsaPutYOmiteLaFechaCuandoEsNula() = runBlocking {
        servidor.enqueue(MockResponse().setResponseCode(200).setBody(lunaJson))

        val resultado = repositorio.editarMascota("m1", MascotaInput("Luna II", "Gato", 4, true, null))

        assertEquals(AppResult.Success(luna), resultado)
        val peticion = servidor.takeRequest()
        assertEquals("PUT", peticion.method)
        assertEquals("/mascotas/m1", peticion.path)
        // Sin fecha de visita el campo no viaja: la API lo interpreta como "sin fecha".
        assertEquals(
            """{"nombre":"Luna II","especie":"Gato","edadAnios":4,"estaVacunado":true}""",
            peticion.body.readUtf8(),
        )
    }

    @Test
    fun registrarAlimentacionUsaPatchConLaHoraEnUtc() = runBlocking {
        servidor.enqueue(MockResponse().setResponseCode(200).setBody(bobJson))

        val resultado = repositorio.registrarAlimentacion(
            "m2",
            UltimaAlimentacion("Croquetas", "250 g", Instant.parse("2026-11-05T14:30:00Z")),
        )

        assertEquals(AppResult.Success(bob), resultado)
        val peticion = servidor.takeRequest()
        assertEquals("PATCH", peticion.method)
        assertEquals("/mascotas/m2", peticion.path)
        assertEquals(
            """{"tipoAlimento":"Croquetas","cantidadAlimento":"250 g","fechaHoraUltimaAlimentacion":"2026-11-05T14:30:00Z"}""",
            peticion.body.readUtf8(),
        )
    }

    @Test
    fun eliminarMascotaUsaDeleteYAceptaUnaRespuestaSinCuerpo() = runBlocking {
        servidor.enqueue(MockResponse().setResponseCode(204))

        val resultado = repositorio.eliminarMascota("m1")

        assertEquals(AppResult.Success(Unit), resultado)
        val peticion = servidor.takeRequest()
        assertEquals("DELETE", peticion.method)
        assertEquals("/mascotas/m1", peticion.path)
    }

    @Test
    fun todasLasPeticionesLlevanElTokenEnElEncabezado() = runBlocking {
        servidor.enqueue(MockResponse().setResponseCode(200).setBody("[]"))
        servidor.enqueue(MockResponse().setResponseCode(200).setBody(lunaJson))

        repositorio.obtenerMascotas()
        repositorio.obtenerMascota("m1")

        assertEquals("Bearer token-abc", servidor.takeRequest().getHeader("Authorization"))
        assertEquals("Bearer token-abc", servidor.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun tokenExpiradoDevuelveUnauthorized() = runBlocking {
        servidor.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("""{"codigo":"TOKEN_INVALIDO","mensaje":"Sesión inválida o expirada"}"""),
        )

        assertEquals(AppResult.Failure(DataError.Unauthorized), repositorio.obtenerMascotas())
    }

    @Test
    fun mascotaInexistenteDevuelveNotFound() = runBlocking {
        servidor.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("""{"codigo":"NO_ENCONTRADO","mensaje":"Mascota no encontrada"}"""),
        )

        assertEquals(AppResult.Failure(DataError.NotFound), repositorio.obtenerMascota("nada"))
    }

    @Test
    fun datosInvalidosDevuelvenLosCampos() = runBlocking {
        servidor.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody(
                    """{"codigo":"VALIDACION","mensaje":"Hay datos inválidos","campos":[{"campo":"edadAnios","mensaje":"Debe ser menor o igual a 30"}]}""",
                ),
        )

        val resultado = repositorio.crearMascota(MascotaInput("Luna", "Gato", 31, true, null))

        assertEquals(
            AppResult.Failure(
                DataError.Validation(listOf(FieldError("edadAnios", "Debe ser menor o igual a 30"))),
            ),
            resultado,
        )
    }

    @Test
    fun unaFechaMalFormadaEnLaRespuestaEsSerialization() = runBlocking {
        servidor.enqueue(
            MockResponse().setResponseCode(200).setBody(lunaJson.replace("2020-01-01", "ayer").let { "[$it]" }),
        )

        assertEquals(AppResult.Failure(DataError.Serialization), repositorio.obtenerMascotas())
    }

    @Test
    fun sinServidorDevuelveNoInternet() = runBlocking {
        // El puerto 1 rechaza la conexión de inmediato.
        val sinServidor = crearRepositorio("http://localhost:1/")

        assertEquals(AppResult.Failure(DataError.NoInternet), sinServidor.obtenerMascotas())
    }
}