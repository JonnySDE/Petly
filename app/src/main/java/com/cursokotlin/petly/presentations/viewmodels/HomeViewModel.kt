package com.cursokotlin.petly.presentations.viewmodels

import androidx.lifecycle.ViewModel
import com.cursokotlin.petly.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeUiState(
    val nombreUsuario: String = "",
    val sesionCerrada: Boolean = false,
)

class HomeViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(nombreUsuario = authRepository.nombreUsuario().orEmpty()),
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onCerrarSesionClick() {
        authRepository.cerrarSesion()
        _uiState.update { it.copy(sesionCerrada = true) }
    }

    fun onNavegacionConsumida() {
        _uiState.update { it.copy(sesionCerrada = false) }
    }
}