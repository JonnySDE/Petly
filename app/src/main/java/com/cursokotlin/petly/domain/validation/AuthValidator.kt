package com.cursokotlin.petly.domain.validation

/** Reglas de login y registro. Cada función devuelve null si el valor es válido. */
object AuthValidator {
    const val MAX_NOMBRE = 50
    const val MIN_CONTRASENA = 8

    /** Límite de bcrypt en la API: más de 72 bytes se rechaza. */
    const val MAX_CONTRASENA_BYTES = 72

    private val CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$")

    fun validarNombre(nombre: String): ValidationError? = when {
        nombre.isBlank() -> ValidationError.Required
        nombre.trim().length > MAX_NOMBRE -> ValidationError.TooLong
        else -> null
    }

    fun validarCorreo(correo: String): ValidationError? = when {
        correo.isBlank() -> ValidationError.Required
        !CORREO.matches(correo.trim()) -> ValidationError.InvalidEmail
        else -> null
    }

    /** Contraseña de una cuenta nueva (registro). */
    fun validarContrasenaNueva(contrasena: String): ValidationError? = when {
        contrasena.isEmpty() -> ValidationError.Required
        contrasena.length < MIN_CONTRASENA -> ValidationError.PasswordTooShort
        contrasena.toByteArray().size > MAX_CONTRASENA_BYTES -> ValidationError.PasswordTooLong
        else -> null
    }

    fun validarConfirmacion(contrasena: String, confirmacion: String): ValidationError? = when {
        confirmacion.isEmpty() -> ValidationError.Required
        confirmacion != contrasena -> ValidationError.PasswordMismatch
        else -> null
    }

    /** En el login solo se exige que no esté vacía: la API decide si es correcta. */
    fun validarContrasenaLogin(contrasena: String): ValidationError? =
        if (contrasena.isEmpty()) ValidationError.Required else null
}