package com.cursokotlin.petly.domain.repository

import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.model.User

interface AuthRepository {
    /** Crea la cuenta. No inicia sesión: la app envía al usuario al login. */
    suspend fun registrar(nombre: String, correo: String, contrasena: String): AppResult<Unit>

    /** Inicia sesión y deja el token guardado; el ViewModel nunca lo ve. */
    suspend fun iniciarSesion(correo: String, contrasena: String): AppResult<User>

    /** Borra la sesión guardada. */
    fun cerrarSesion()

    fun haySesion(): Boolean

    /** Nombre del usuario con sesión activa, para el saludo del Inicio. */
    fun nombreUsuario(): String?
}