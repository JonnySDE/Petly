
package com.cursokotlin.petly.fakes

import com.cursokotlin.petly.data.local.SessionStorage

/** Sesión en memoria para las pruebas (se reutilizará con los repositorios y ViewModels). */
class FakeSessionStorage : SessionStorage {
    private var token: String? = null
    private var nombre: String? = null

    override fun guardarSesion(token: String, nombreUsuario: String) {
        this.token = token
        this.nombre = nombreUsuario
    }

    override fun obtenerToken(): String? = token

    override fun obtenerNombreUsuario(): String? = nombre

    override fun haySesion(): Boolean = token != null

    override fun borrarSesion() {
        token = null
        nombre = null
    }
}