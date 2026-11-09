package com.cursokotlin.petly.presentations.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursokotlin.petly.R
import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.model.DataError
import com.cursokotlin.petly.domain.model.Mascota
import com.cursokotlin.petly.domain.model.MascotaInput
import com.cursokotlin.petly.domain.repository.AuthRepository
import com.cursokotlin.petly.domain.repository.MascotaRepository
import com.cursokotlin.petly.domain.validation.MascotaValidator
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Especies que ofrece el selector. La API acepta cualquier texto. */
val ESPECIES_DISPONIBLES = listOf(
    "Perro", "Gato", "Ave", "Pez", "Conejo", "Hámster",
    "Tortuga", "Iguana", "Serpiente", "Tarántula", "Otro",
)

data class FormUiState(
    val modoEdicion: Boolean = false,
    /** En edición, mientras se descargan los datos actuales de la mascota. */
    val isLoadingInicial: Boolean = false,
    /** Error al descargar los datos en edición: la pantalla ofrece reintentar. */
    @StringRes val errorCarga: Int? = null,
    val nombre: String = "",
    val especie: String = ESPECIES_DISPONIBLES.first(),
    val especies: List<String> = ESPECIES_DISPONIBLES,
    val edad: String = "",
    val estaVacunado: Boolean = false,
    val fechaVisita: LocalDate? = null,
    @StringRes val nombreError: Int? = null,
    @StringRes val especieError: Int? = null,
    @StringRes val edadError: Int? = null,
    @StringRes val fechaError: Int? = null,
    val isSaving: Boolean = false,
    @StringRes val mensaje: Int? = null,
    /** Mensaje de éxito para la pantalla a la que se vuelve. La pantalla navega y avisa con onGuardadoConsumido(). */
    @StringRes val guardadoMensaje: Int? = null,
    val sesionCerrada: Boolean = false,
) {
    /** Fecha en el formato del diseño (2026-05-12); vacío si no hay fecha. */
    val fechaVisitaTexto: String get() = fechaVisita?.toString().orEmpty()

    /** Lo que espera el selector de fechas: milisegundos UTC del inicio del día. */
    val fechaVisitaMillis: Long?
        get() = fechaVisita?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
}

/** Un solo formulario para crear (mascotaId = null) y editar (mascotaId = id). */
class FormViewModel(
    private val mascotaId: String?,
    private val mascotaRepository: MascotaRepository,
    private val authRepository: AuthRepository,
    private val clock: Clock,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FormUiState(modoEdicion = mascotaId != null, isLoadingInicial = mascotaId != null),
    )
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    init {
        if (mascotaId != null) cargarMascota(mascotaId)
    }

    fun onNombreChange(valor: String) {
        _uiState.update { it.copy(nombre = valor, nombreError = null) }
    }

    fun onEspecieChange(valor: String) {
        _uiState.update { it.copy(especie = valor, especieError = null) }
    }

    fun onEdadChange(valor: String) {
        _uiState.update { it.copy(edad = valor, edadError = null) }
    }

    fun onVacunadoChange(valor: Boolean) {
        _uiState.update { it.copy(estaVacunado = valor) }
    }

    /** El selector de fechas entrega milisegundos UTC; null si no se eligió ninguna. */
    fun onFechaVisitaSeleccionada(millis: Long?) {
        val fecha = millis?.let { Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate() }
        _uiState.update { it.copy(fechaVisita = fecha, fechaError = null) }
    }

    fun onQuitarFechaClick() {
        _uiState.update { it.copy(fechaVisita = null, fechaError = null) }
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

    fun onReintentarCargaClick() {
        if (mascotaId != null) cargarMascota(mascotaId)
    }

    fun onGuardarClick() {
        val estado = _uiState.value
        if (estado.isSaving || estado.isLoadingInicial) return

        val nombreError = MascotaValidator.validarNombre(estado.nombre)?.toMessageRes()
        val especieError = MascotaValidator.validarEspecie(estado.especie)?.toMessageRes()
        val edadError = MascotaValidator.validarEdad(estado.edad)?.toMessageRes()
        val fechaError = MascotaValidator.validarFechaVisita(estado.fechaVisita, LocalDate.now(clock))?.toMessageRes()

        if (listOf(nombreError, especieError, edadError, fechaError).any { it != null }) {
            _uiState.update {
                it.copy(
                    nombreError = nombreError,
                    especieError = especieError,
                    edadError = edadError,
                    fechaError = fechaError,
                )
            }
            return
        }

        val datos = MascotaInput(
            nombre = estado.nombre.trim(),
            especie = estado.especie.trim(),
            edadAnios = estado.edad.trim().toInt(), // ya validada: es un entero entre 0 y 30
            estaVacunado = estado.estaVacunado,
            fechaUltimaVisita = estado.fechaVisita,
        )

        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val resultado = if (mascotaId == null) {
                mascotaRepository.crearMascota(datos)
            } else {
                mascotaRepository.editarMascota(mascotaId, datos)
            }
            when (resultado) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        isSaving = false,
                        guardadoMensaje = if (mascotaId == null) R.string.pet_created else R.string.pet_updated,
                    )
                }
                is AppResult.Failure -> manejarError(resultado.error, alCargar = false)
            }
        }
    }

    private fun cargarMascota(id: String) {
        _uiState.update { it.copy(isLoadingInicial = true, errorCarga = null) }
        viewModelScope.launch {
            when (val resultado = mascotaRepository.obtenerMascota(id)) {
                is AppResult.Success -> _uiState.update { precargar(it, resultado.data) }
                is AppResult.Failure -> manejarError(resultado.error, alCargar = true)
            }
        }
    }

    private fun precargar(estado: FormUiState, mascota: Mascota): FormUiState = estado.copy(
        isLoadingInicial = false,
        nombre = mascota.nombre,
        especie = mascota.especie,
        // Si la mascota tiene una especie que no está en la lista, se agrega para no perderla.
        especies = (ESPECIES_DISPONIBLES + mascota.especie).distinct(),
        edad = mascota.edadAnios.toString(),
        estaVacunado = mascota.estaVacunado,
        fechaVisita = mascota.fechaUltimaVisita,
    )

    private fun manejarError(error: DataError, alCargar: Boolean) {
        // Token inválido o expirado: se borra la sesión y se vuelve al login.
        if (error == DataError.Unauthorized) {
            authRepository.cerrarSesion()
            _uiState.update { it.copy(isLoadingInicial = false, isSaving = false, sesionCerrada = true) }
            return
        }
        _uiState.update {
            if (alCargar) {
                it.copy(isLoadingInicial = false, errorCarga = error.toMessageRes())
            } else {
                it.copy(isSaving = false, mensaje = error.toMessageRes())
            }
        }
    }
}