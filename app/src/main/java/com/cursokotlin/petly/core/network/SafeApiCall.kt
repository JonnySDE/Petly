package com.cursokotlin.petly.core.network

import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.model.DataError
import com.cursokotlin.petly.domain.model.FieldError
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import retrofit2.HttpException

private val errorJson = Json { ignoreUnknownKeys = true }

/**
 * Ejecuta una llamada a la API y convierte cualquier fallo en un AppResult.Failure tipado.
 * La cancelación de la corrutina nunca se oculta: se relanza.
 */
suspend fun <T> safeApiCall(call: suspend () -> T): AppResult<T> =
    try {
        AppResult.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AppResult.Failure(e.toDataError())
    }

/** Traduce una excepción de red a un DataError. El orden importa: el timeout es un IOException. */
internal fun Throwable.toDataError(): DataError = when (this) {
    is SocketTimeoutException -> DataError.Timeout
    is IOException -> DataError.NoInternet
    is HttpException -> toHttpDataError()
    is SerializationException -> DataError.Serialization
    else -> DataError.Unknown
}

/** Usa el código de error que manda la API; si el cuerpo no es JSON, decide por el número HTTP. */
private fun HttpException.toHttpDataError(): DataError {
    val cuerpo = runCatching { response()?.errorBody()?.string() }.getOrNull()
    val error = cuerpo?.let {
        runCatching { errorJson.decodeFromString(ErrorDto.serializer(), it) }.getOrNull()
    }
    return when (error?.codigo) {
        "VALIDACION" -> DataError.Validation(
            error?.campos.orEmpty().map { FieldError(it.campo, it.mensaje) },
        )
        "CREDENCIALES_INVALIDAS" -> DataError.InvalidCredentials
        "TOKEN_INVALIDO" -> DataError.Unauthorized
        "CORREO_DUPLICADO" -> DataError.EmailTaken
        "NO_ENCONTRADO" -> DataError.NotFound
        else -> when (code()) {
            401 -> DataError.Unauthorized
            404 -> DataError.NotFound
            else -> DataError.Http(code())
        }
    }
}