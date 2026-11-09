package com.cursokotlin.petly.presentations.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursokotlin.petly.R
import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.model.DataError
import com.cursokotlin.petly.domain.model.UltimaAlimentacion
import com.cursokotlin.petly.domain.repository.AuthRepository
import com.cursokotlin.petly.domain.repository.MascotaRepository
import com.cursokotlin.petly.domain.validation.MascotaValidator
import java.time.Clock
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedingUiState(
    val isLoadingInicial: Boolean = true,
    @StringRes val errorCarga: Int? = null,
    val nombreMascota: String = "",
    val especie: String = "",
    val tipoAlimento: String = "",
    val cantidad: String = "",
    @StringRes val tipoError: Int? = null,
    @StringRes val cantidadError: Int? = null,
    val horaTexto: String = "",
    val isSaving: Boolean = false,
    @StringRes val mensaje: Int? = null,
    @StringRes val guardadoMensaje: Int? = null,
    val sesionCerrada: Boolean = false,
)

class FeedingViewModel(
    private val mascotaId: String,
    private val mascotaRepository: MascotaRepository,
    private val authRepository: AuthRepository,
    private val clock: Clock,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedingUiState(horaTexto = horaActualTexto()))
    val uiState: StateFlow<FeedingUiState> = _uiState.asStateFlow()

    init {
        cargarMascota()
    }

    fun onTipoChange(valor: String) {
        _uiState.update { it.copy(tipoAlimento = valor, tipoError = null) }
    }

    fun onCantidadChange(valor: String) {
        _uiState.update { it.copy(cantidad = valor, cantidadError = null) }
    }

    fun onReintentarCargaClick() = cargarMascota()

    fun onGuardarClick() {
        val estado = _uiState.value
        if (estado.isSaving || estado.isLoadingInicial || estado.errorCarga != null) return

        val tipoError = MascotaValidator.validarTipoAlimento(estado.tipoAlimento)?.toMessageRes()
        val cantidadError = MascotaValidator.validarCantidad(estado.cantidad)?.toMessageRes()
        if (tipoError != null || cantidadError != null) {
            _uiState.update { it.copy(tipoError = tipoError, cantidadError = cantidadError) }
            return
        }

        // La hora se toma aquí, al confirmar.
        val alimentacion = UltimaAlimentacion(
            tipoAlimento = estado.tipoAlimento.trim(),
            cantidad = estado.cantidad.trim(),
            fechaHora = clock.instant(),
        )
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            when (val resultado = mascotaRepository.registrarAlimentacion(mascotaId, alimentacion)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(isSaving = false, guardadoMensaje = R.string.feeding_registered)
                }
                is AppResult.Failure -> manejarError(resultado.error)
            }
        }
    }

    fun onMensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }

    fun onGuardadoConsumido() {
        _uiState.update { it.copy(guardadoMensaje = null) }
    }

    fun onNavegacionConsumida() {
        _uiState.update { it.copy(sesionCerrada = false) }
    }

    private fun cargarMascota() {
        _uiState.update { it.copy(isLoadingInicial = true, errorCarga = null) }
        viewModelScope.launch {
            when (val resultado = mascotaRepository.obtenerMascota(mascotaId)) {
                is AppResult.Success -> {
                    val mascota = resultado.data
                    val previa = mascota.ultimaAlimentacion
                    _uiState.update { estado ->
                        estado.copy(
                            isLoadingInicial = false,
                            nombreMascota = mascota.nombre,
                            especie = mascota.especie,
                            tipoAlimento = estado.tipoAlimento.ifEmpty { previa?.tipoAlimento.orEmpty() },
                            cantidad = estado.cantidad.ifEmpty { previa?.cantidad.orEmpty() },
                        )
                    }
                }
                is AppResult.Failure -> manejarError(resultado.error)
            }
        }
    }

    private fun manejarError(error: DataError) {
        if (error == DataError.Unauthorized) {
            authRepository.cerrarSesion()
            _uiState.update { it.copy(isLoadingInicial = false, isSaving = false, sesionCerrada = true) }
            return
        }
        _uiState.update { estado ->
            if (estado.nombreMascota.isEmpty()) {
                estado.copy(isLoadingInicial = false, isSaving = false, errorCarga = error.toMessageRes())
            } else {
                estado.copy(isLoadingInicial = false, isSaving = false, mensaje = error.toMessageRes())
            }
        }
    }

    private fun horaActualTexto(): String =
        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(clock.instant().atZone(clock.zone))
}