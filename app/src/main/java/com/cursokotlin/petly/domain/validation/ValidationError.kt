package com.cursokotlin.petly.domain.validation

/** Motivo por el que un campo no es válido. */
enum class ValidationError {
    Required,
    TooLong,
    InvalidEmail,
    PasswordTooShort,
    PasswordTooLong,
    PasswordMismatch,
    InvalidNumber,
    AgeOutOfRange,
    FutureDate,
}