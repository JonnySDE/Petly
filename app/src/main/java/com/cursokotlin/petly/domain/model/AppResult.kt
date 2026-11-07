package com.cursokotlin.petly.domain.model

/** Resultado de una operación de datos: o trae el dato o trae un error tipado. */
sealed interface AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>
    data class Failure(val error: DataError) : AppResult<Nothing>
}