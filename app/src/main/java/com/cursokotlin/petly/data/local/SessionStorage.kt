package com.cursokotlin.petly.data.local

/** Almacenamiento de la sesión. El resto de la app depende de esta interfaz, no de SharedPreferences. */
interface SessionStorage {
    fun guardarSesion(token: String, nombreUsuario: String)

    fun obtenerToken(): String?

    fun obtenerNombreUsuario(): String?

    fun haySesion(): Boolean

    fun borrarSesion()
}