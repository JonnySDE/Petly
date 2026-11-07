package com.cursokotlin.petly.data.local

import android.content.SharedPreferences
import androidx.core.content.edit

class SessionStorageImpl(
    private val prefs: SharedPreferences,
) : SessionStorage {

    override fun guardarSesion(token: String, nombreUsuario: String) {
        prefs.edit {
            putString(KEY_TOKEN, token)
            putString(KEY_NOMBRE, nombreUsuario)
        }
    }

    override fun obtenerToken(): String? = prefs.getString(KEY_TOKEN, null)

    override fun obtenerNombreUsuario(): String? = prefs.getString(KEY_NOMBRE, null)

    override fun haySesion(): Boolean = !obtenerToken().isNullOrBlank()

    override fun borrarSesion() {
        prefs.edit { clear() }
    }

    private companion object {
        const val KEY_TOKEN = "token"
        const val KEY_NOMBRE = "nombre_usuario"
    }
}