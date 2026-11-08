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

data class HomeUiState(
    val nombreUsuario: String = "",
    val iniciales: String = "",
    val isLoading: Boolean = false,
    val mascotas: List<MascotaItemUi> = emptyList(),
    @StringRes val errorMessage: Int? = null,
    @StringRes val mensaje: Int? = null,
    val sesionCerrada: Boolean = false,
)

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val mascotaRepository: MascotaRepository,
    private val clock: Clock,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        authRepository.nombreUsuario().orEmpty().let { nombre ->
            HomeUiState(nombreUsuario = nombre, iniciales = inicialesDe(nombre), isLoading = true)
        },
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        cargarMascotas()
    }

    fun onReintentarClick() = cargarMascotas()

    fun onCerrarSesionClick() {
        authRepository.cerrarSesion()
        _uiState.update { it.copy(sesionCerrada = true) }
    }

    fun onMensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }

    fun onNavegacionConsumida() {
        _uiState.update { it.copy(sesionCerrada = false) }
    }

    private fun cargarMascotas() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            when (val resultado = mascotaRepository.obtenerMascotas()) {
                is AppResult.Success -> _uiState.update { estado ->
                    estado.copy(
                        isLoading = false,
                        mascotas = resultado.data.map { it.toItemUi(clock) },
                    )
                }
                is AppResult.Failure -> manejarError(resultado.error)
            }
        }
    }

    private fun manejarError(error: DataError) {
        if (error == DataError.Unauthorized) {
            authRepository.cerrarSesion()
            _uiState.update { it.copy(isLoading = false, sesionCerrada = true) }
            return
        }
        _uiState.update { estado ->
            if (estado.mascotas.isEmpty()) {
                estado.copy(isLoading = false, errorMessage = error.toMessageRes())
            } else {
                // Ya hay una lista visible: se conserva y el error va en un Snackbar.
                estado.copy(isLoading = false, mensaje = error.toMessageRes())
            }
        }
    }
}