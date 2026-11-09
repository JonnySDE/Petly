package com.cursokotlin.petly.presentations.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.model.DataError
import com.cursokotlin.petly.domain.repository.AuthRepository
import com.cursokotlin.petly.domain.repository.MascotaRepository
import java.time.Clock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = true,
    val mascota: MascotaDetalleUi? = null,
    /** Error de carga SIN ficha que mostrar: la pantalla ofrece reintentar. */
    @StringRes val errorMessage: Int? = null,
    /** Mensaje para el Snackbar (un fallo al recargar con la ficha ya visible). */
    @StringRes val mensaje: Int? = null,
    /** La pantalla navega al login cuando esto es true y avisa con onNavegacionConsumida(). */
    val sesionCerrada: Boolean = false,
)

class DetailViewModel(
    private val mascotaId: String,
    private val mascotaRepository: MascotaRepository,
    private val authRepository: AuthRepository,
    private val clock: Clock,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        cargarMascota()
    }

    fun onReintentarClick() = cargarMascota()

    fun onActualizar() = cargarMascota()

    fun onMensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }

    fun onNavegacionConsumida() {
        _uiState.update { it.copy(sesionCerrada = false) }
    }

    private fun cargarMascota() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            when (val resultado = mascotaRepository.obtenerMascota(mascotaId)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(isLoading = false, mascota = resultado.data.toDetalleUi(clock))
                }
                is AppResult.Failure -> manejarError(resultado.error)
            }
        }
    }

    private fun manejarError(error: DataError) {
        // Token inválido o expirado: se borra la sesión y se vuelve al login.
        if (error == DataError.Unauthorized) {
            authRepository.cerrarSesion()
            _uiState.update { it.copy(isLoading = false, sesionCerrada = true) }
            return
        }
        _uiState.update { estado ->
            if (estado.mascota == null) {
                estado.copy(isLoading = false, errorMessage = error.toMessageRes())
            } else {
                // Ya hay una ficha visible: se conserva y el error va en un Snackbar.
                estado.copy(isLoading = false, mensaje = error.toMessageRes())
            }
        }
    }
}