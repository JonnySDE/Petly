package com.cursokotlin.petly.data.repository

import com.cursokotlin.petly.core.network.safeApiCall
import com.cursokotlin.petly.data.datasource.AuthApi
import com.cursokotlin.petly.data.local.SessionStorage
import com.cursokotlin.petly.data.mapper.loginRequest
import com.cursokotlin.petly.data.mapper.registerRequest
import com.cursokotlin.petly.data.mapper.toDomain
import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.model.User
import com.cursokotlin.petly.domain.repository.AuthRepository
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val sessionStorage: SessionStorage,
) : AuthRepository {

    override suspend fun registrar(
        nombre: String,
        correo: String,
        contrasena: String,
    ): AppResult<Unit> = safeApiCall {
        api.register(registerRequest(nombre, correo, contrasena))
        Unit // el DTO no sale de la capa de datos
    }

    override suspend fun iniciarSesion(correo: String, contrasena: String): AppResult<User> =
        safeApiCall {
            val respuesta = api.login(loginRequest(correo, contrasena))
            // Response<T> no lanza excepción en 4xx/5xx: se lanza aquí para que safeApiCall la traduzca.
            if (!respuesta.isSuccessful) throw HttpException(respuesta)

            val token = respuesta.headers()["Authorization"]?.removePrefix("Bearer ")?.trim()
            val usuario = respuesta.body()?.toDomain()
            if (token.isNullOrBlank() || usuario == null) {
                error("La respuesta del login no trae token o usuario")
            }
            sessionStorage.guardarSesion(token, usuario.nombre)
            usuario
        }

    override fun cerrarSesion() = sessionStorage.borrarSesion()

    override fun haySesion(): Boolean = sessionStorage.haySesion()

    override fun nombreUsuario(): String? = sessionStorage.obtenerNombreUsuario()
}