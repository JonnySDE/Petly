package com.cursokotlin.petly.domain.model

/** Errores que la capa de datos entrega a los ViewModels. No contienen textos: la UI los traduce. */
sealed interface DataError {
    /** Sin conexión con el servidor. */
    data object NoInternet : DataError

    data object Timeout : DataError

    /** Login con correo o contraseña incorrectos. */
    data object InvalidCredentials : DataError

    /** Token ausente, inválido o expirado: hay que volver a iniciar sesión. */
    data object Unauthorized : DataError

    /** Registro con un correo que ya existe. */
    data object EmailTaken : DataError

    data object NotFound : DataError

    /** El servidor rechazó datos; trae el detalle por campo. */
    data class Validation(val fields: List<FieldError>) : DataError

    /** Cualquier otro código HTTP (por ejemplo, 5xx). */
    data class Http(val code: Int) : DataError

    data object Serialization : DataError

    data object Unknown : DataError
}

data class FieldError(
    val field: String,
    val message: String,
)