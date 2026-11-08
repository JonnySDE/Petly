package com.cursokotlin.petly.presentations.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursokotlin.petly.R
import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.model.DataError
import com.cursokotlin.petly.domain.repository.AuthRepository
import com.cursokotlin.petly.domain.validation.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val confirmacion: String = "",
    @StringRes val nombreError: Int? = null,
    @StringRes val correoError: Int? = null,
    @StringRes val contrasenaError: Int? = null,
    @StringRes val confirmacionError: Int? = null,
    val isLoading: Boolean = false,
    @StringRes val mensaje: Int? = null,
    /** La pantalla navega al login cuando esto es true y avisa con onNavegacionConsumida(). */
    val registroExitoso: Boolean = false,
)

class RegisterViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNombreChange(valor: String) {
        _uiState.update { it.copy(nombre = valor, nombreError = null) }
    }

    fun onCorreoChange(valor: String) {
        _uiState.update { it.copy(correo = valor, correoError = null) }
    }

    fun onContrasenaChange(valor: String) {
        _uiState.update { it.copy(contrasena = valor, contrasenaError = null, confirmacionError = null) }
    }

    fun onConfirmacionChange(valor: String) {
        _uiState.update { it.copy(confirmacion = valor, confirmacionError = null) }
    }

    fun onMensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }

    fun onNavegacionConsumida() {
        _uiState.update { it.copy(registroExitoso = false) }
    }

    fun onRegisterClick() {
        val estado = _uiState.value
        if (estado.isLoading) return

        val nombreError = AuthValidator.validarNombre(estado.nombre)?.toMessageRes()
        val correoError = AuthValidator.validarCorreo(estado.correo)?.toMessageRes()
        val contrasenaError = AuthValidator.validarContrasenaNueva(estado.contrasena)?.toMessageRes()
        val confirmacionError =
            AuthValidator.validarConfirmacion(estado.contrasena, estado.confirmacion)?.toMessageRes()

        if (listOf(nombreError, correoError, contrasenaError, confirmacionError).any { it != null }) {
            _uiState.update {
                it.copy(
                    nombreError = nombreError,
                    correoError = correoError,
                    contrasenaError = contrasenaError,
                    confirmacionError = confirmacionError,
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            when (val resultado = authRepository.registrar(estado.nombre, estado.correo, estado.contrasena)) {
                // Formulario limpio: no se conservan contraseñas en memoria.
                is AppResult.Success -> _uiState.update { RegisterUiState(registroExitoso = true) }
                is AppResult.Failure -> when (resultado.error) {
                    // El correo repetido se muestra junto al campo, no en un Snackbar.
                    DataError.EmailTaken -> _uiState.update {
                        it.copy(isLoading = false, correoError = R.string.error_email_taken)
                    }
                    else -> _uiState.update {
                        it.copy(isLoading = false, mensaje = resultado.error.toMessageRes())
                    }
                }
            }
        }
    }
}