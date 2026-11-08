package com.cursokotlin.petly.presentations.viewmodels

import androidx.annotation.StringRes
import com.cursokotlin.petly.R
import com.cursokotlin.petly.domain.model.DataError
import com.cursokotlin.petly.domain.validation.ValidationError

@StringRes
fun ValidationError.toMessageRes(): Int = when (this) {
    ValidationError.Required -> R.string.error_required
    ValidationError.TooLong -> R.string.error_too_long
    ValidationError.InvalidEmail -> R.string.error_invalid_email
    ValidationError.PasswordTooShort -> R.string.error_password_too_short
    ValidationError.PasswordTooLong -> R.string.error_password_too_long
    ValidationError.PasswordMismatch -> R.string.error_password_mismatch
    ValidationError.InvalidNumber -> R.string.error_invalid_number
    ValidationError.AgeOutOfRange -> R.string.error_age_range
    ValidationError.FutureDate -> R.string.error_future_date
}

@StringRes
fun DataError.toMessageRes(): Int = when (this) {
    DataError.NoInternet -> R.string.error_no_internet
    DataError.Timeout -> R.string.error_timeout
    DataError.InvalidCredentials -> R.string.error_invalid_credentials
    DataError.Unauthorized -> R.string.error_session_expired
    DataError.EmailTaken -> R.string.error_email_taken
    DataError.NotFound -> R.string.error_not_found
    is DataError.Validation -> R.string.error_validation
    is DataError.Http -> R.string.error_server
    DataError.Serialization -> R.string.error_server
    DataError.Unknown -> R.string.error_unknown
}