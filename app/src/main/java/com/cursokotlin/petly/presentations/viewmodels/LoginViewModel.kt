package com.cursokotlin.petly.presentations.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.repository.AuthRepository
import com.cursokotlin.petly.domain.validation.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    @StringRes val correoError: Int? = null,
    @StringRes val contrasenaError: Int? = null,
    val isLoading: Boolean = false,
    /** Mensaje para el Snackbar; la pantalla lo muestra y avisa con onMensajeMostrado(). */
    @StringRes val mensaje: Int? = null,
    /** La pantalla navega cuando esto es true y avisa con onNavegacionConsumida(). */
    val sesionIniciada: Boolean = false,
)

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoChange(valor: String) {
        _uiState.update { it.copy(correo = valor, correoError = null) }
    }

    fun onContrasenaChange(valor: String) {
        _uiState.update { it.copy(contrasena = valor, contrasenaError = null) }
    }

    fun onMensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }

    fun onNavegacionConsumida() {
        _uiState.update { it.copy(sesionIniciada = false) }
    }

    fun onLoginClick() {
        val estado = _uiState.value
        if (estado.isLoading) return

        val correoError = AuthValidator.validarCorreo(estado.correo)?.toMessageRes()
        val contrasenaError = AuthValidator.validarContrasenaLogin(estado.contrasena)?.toMessageRes()
        if (correoError != null || contrasenaError != null) {
            _uiState.update { it.copy(correoError = correoError, contrasenaError = contrasenaError) }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            when (val resultado = authRepository.iniciarSesion(estado.correo, estado.contrasena)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(isLoading = false, contrasena = "", sesionIniciada = true)
                }
                is AppResult.Failure -> _uiState.update {
                    it.copy(isLoading = false, mensaje = resultado.error.toMessageRes())
                }
            }
        }
    }
}