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
import com.cursokotlin.petly.R

data class HomeUiState(
    val nombreUsuario: String = "",
    val iniciales: String = "",
    val isLoading: Boolean = false,
    val mascotas: List<MascotaItemUi> = emptyList(),
    val mascotaAEliminar: MascotaItemUi? = null,
    val isDeleting: Boolean = false,
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

    fun onActualizar() = cargarMascotas()

    fun onCerrarSesionClick() {
        authRepository.cerrarSesion()
        _uiState.update { it.copy(sesionCerrada = true) }
    }
    fun onEliminarClick(mascota: MascotaItemUi) {
        _uiState.update { it.copy(mascotaAEliminar = mascota) }
    }

    fun onCancelarEliminar() {
        if (_uiState.value.isDeleting) return
        _uiState.update { it.copy(mascotaAEliminar = null) }
    }

    fun onConfirmarEliminar() {
        val mascota = _uiState.value.mascotaAEliminar ?: return
        if (_uiState.value.isDeleting) return
        _uiState.update { it.copy(isDeleting = true) }
        viewModelScope.launch {
            when (val resultado = mascotaRepository.eliminarMascota(mascota.id)) {
                is AppResult.Success -> _uiState.update { estado ->
                    estado.copy(
                        isDeleting = false,
                        mascotaAEliminar = null,
                        mascotas = estado.mascotas.filterNot { it.id == mascota.id },
                        mensaje = R.string.pet_deleted,
                    )
                }
                is AppResult.Failure -> {
                    _uiState.update { it.copy(isDeleting = false, mascotaAEliminar = null) }
                    manejarError(resultado.error)
                }
            }
        }
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