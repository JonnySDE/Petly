package com.cursokotlin.petly.fakes

import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.model.User
import com.cursokotlin.petly.domain.repository.AuthRepository

/** Repositorio de autenticación para las pruebas: se le dice qué resultado devolver. */
class FakeAuthRepository : AuthRepository {
    var resultadoLogin: AppResult<User> = AppResult.Success(User("u1", "Ana", "ana@correo.com"))
    var resultadoRegistro: AppResult<Unit> = AppResult.Success(Unit)

    var llamadasLogin = 0
    var ultimoLogin: Pair<String, String>? = null
    var llamadasRegistro = 0
    var ultimoRegistro: Triple<String, String, String>? = null

    override suspend fun registrar(nombre: String, correo: String, contrasena: String): AppResult<Unit> {
        llamadasRegistro++
        ultimoRegistro = Triple(nombre, correo, contrasena)
        return resultadoRegistro
    }

    override suspend fun iniciarSesion(correo: String, contrasena: String): AppResult<User> {
        llamadasLogin++
        ultimoLogin = correo to contrasena
        return resultadoLogin
    }

    override fun cerrarSesion() = Unit

    override fun haySesion(): Boolean = false

    override fun nombreUsuario(): String? = null
}