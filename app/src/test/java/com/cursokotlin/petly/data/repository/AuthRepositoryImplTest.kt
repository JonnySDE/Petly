package com.cursokotlin.petly.data.repository

import com.cursokotlin.petly.data.datasource.AuthApi
import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.model.DataError
import com.cursokotlin.petly.domain.model.FieldError
import com.cursokotlin.petly.domain.model.User
import com.cursokotlin.petly.fakes.FakeSessionStorage
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

@OptIn(ExperimentalSerializationApi::class)
class AuthRepositoryImplTest {
    private val servidor = MockWebServer()
    private val sesion = FakeSessionStorage()
    private lateinit var repositorio: AuthRepositoryImpl

    private val usuarioJson = """{"id":"u1","nombre":"Ana López","correo":"ana@correo.com"}"""

    @Before
    fun iniciar() {
        servidor.start()
        val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
        val retrofit = Retrofit.Builder()
            .baseUrl(servidor.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        repositorio = AuthRepositoryImpl(retrofit.create<AuthApi>(), sesion)
    }

    @After
    fun detener() {
        servidor.shutdown()
    }

    @Test
    fun loginLeeElTokenDelEncabezadoYGuardaLaSesion() = runBlocking {
        servidor.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Authorization", "Bearer token-abc")
                .setBody(usuarioJson),
        )

        val resultado = repositorio.iniciarSesion("ana@correo.com", "miClave123")

        assertEquals(AppResult.Success(User("u1", "Ana López", "ana@correo.com")), resultado)
        assertEquals("token-abc", sesion.obtenerToken())
        assertEquals("Ana López", sesion.obtenerNombreUsuario())

        val peticion = servidor.takeRequest()
        assertEquals("POST", peticion.method)
        assertEquals("/login", peticion.path)
        assertEquals("""{"correo":"ana@correo.com","contrasena":"miClave123"}""", peticion.body.readUtf8())
    }

    @Test
    fun loginRecortaElCorreoPeroNoLaContrasena() = runBlocking {
        servidor.enqueue(
            MockResponse().setResponseCode(200).setHeader("Authorization", "Bearer t").setBody(usuarioJson),
        )

        repositorio.iniciarSesion("  ana@correo.com ", " clave con espacios ")

        assertEquals(
            """{"correo":"ana@correo.com","contrasena":" clave con espacios "}""",
            servidor.takeRequest().body.readUtf8(),
        )
    }

    @Test
    fun loginConCredencialesIncorrectasNoGuardaSesion() = runBlocking {
        servidor.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("""{"codigo":"CREDENCIALES_INVALIDAS","mensaje":"Correo o contraseña incorrectos"}"""),
        )

        val resultado = repositorio.iniciarSesion("ana@correo.com", "mala")

        assertEquals(AppResult.Failure(DataError.InvalidCredentials), resultado)
        assertFalse(sesion.haySesion())
    }

    @Test
    fun loginSinEncabezadoDeTokenFallaYNoGuardaSesion() = runBlocking {
        servidor.enqueue(MockResponse().setResponseCode(200).setBody(usuarioJson))

        val resultado = repositorio.iniciarSesion("ana@correo.com", "miClave123")

        assertEquals(AppResult.Failure(DataError.Unknown), resultado)
        assertFalse(sesion.haySesion())
    }

    @Test
    fun registrarEnviaLosDatosYNoIniciaSesion() = runBlocking {
        servidor.enqueue(MockResponse().setResponseCode(201).setBody(usuarioJson))

        val resultado = repositorio.registrar(" Ana López ", "ana@correo.com", "miClave123")

        assertEquals(AppResult.Success(Unit), resultado)
        assertFalse(sesion.haySesion())

        val peticion = servidor.takeRequest()
        assertEquals("/register", peticion.path)
        assertEquals(
            """{"nombre":"Ana López","correo":"ana@correo.com","contrasena":"miClave123"}""",
            peticion.body.readUtf8(),
        )
    }

    @Test
    fun registrarConCorreoRepetidoDevuelveEmailTaken() = runBlocking {
        servidor.enqueue(
            MockResponse()
                .setResponseCode(409)
                .setBody("""{"codigo":"CORREO_DUPLICADO","mensaje":"El correo ya está registrado"}"""),
        )

        val resultado = repositorio.registrar("Ana", "ana@correo.com", "miClave123")

        assertEquals(AppResult.Failure(DataError.EmailTaken), resultado)
    }

    @Test
    fun registrarConDatosInvalidosDevuelveLosCampos() = runBlocking {
        servidor.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody(
                    """{"codigo":"VALIDACION","mensaje":"Hay datos inválidos","campos":[{"campo":"correo","mensaje":"Correo inválido"}]}""",
                ),
        )

        val resultado = repositorio.registrar("Ana", "ana@correo.com", "miClave123")

        assertEquals(
            AppResult.Failure(DataError.Validation(listOf(FieldError("correo", "Correo inválido")))),
            resultado,
        )
    }

    @Test
    fun cerrarSesionBorraTokenYNombre() {
        sesion.guardarSesion("token-abc", "Ana López")
        assertTrue(repositorio.haySesion())
        assertEquals("Ana López", repositorio.nombreUsuario())

        repositorio.cerrarSesion()

        assertFalse(repositorio.haySesion())
        assertNull(repositorio.nombreUsuario())
    }
}