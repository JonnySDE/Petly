package com.cursokotlin.petly.fakes

import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.model.DataError
import com.cursokotlin.petly.domain.model.Mascota
import com.cursokotlin.petly.domain.model.MascotaInput
import com.cursokotlin.petly.domain.model.UltimaAlimentacion
import com.cursokotlin.petly.domain.repository.MascotaRepository

/** Repositorio de mascotas para las pruebas. Se amplía en los siguientes commits. */
class FakeMascotaRepository : MascotaRepository {
    var resultadoLista: AppResult<List<Mascota>> = AppResult.Success(emptyList())
    var llamadasLista = 0

    var resultadoDetalle: AppResult<Mascota> = AppResult.Failure(DataError.NotFound)
    var llamadasDetalle = 0
    var ultimoIdConsultado: String? = null

    var resultadoCrear: AppResult<Mascota> = AppResult.Failure(DataError.Unknown)
    var llamadasCrear = 0
    var ultimoCreado: MascotaInput? = null

    var resultadoEditar: AppResult<Mascota> = AppResult.Failure(DataError.Unknown)
    var llamadasEditar = 0
    var ultimaEdicion: Pair<String, MascotaInput>? = null
    var resultadoEliminar: AppResult<Unit> = AppResult.Failure(DataError.Unknown)
    var llamadasEliminar = 0
    var ultimoEliminado: String? = null

    override suspend fun obtenerMascotas(): AppResult<List<Mascota>> {
        llamadasLista++
        return resultadoLista
    }

    override suspend fun obtenerMascota(id: String): AppResult<Mascota> {
        llamadasDetalle++
        ultimoIdConsultado = id
        return resultadoDetalle
    }

    override suspend fun crearMascota(datos: MascotaInput): AppResult<Mascota> {
        llamadasCrear++
        ultimoCreado = datos
        return resultadoCrear
    }

    override suspend fun editarMascota(id: String, datos: MascotaInput): AppResult<Mascota> {
        llamadasEditar++
        ultimaEdicion = id to datos
        return resultadoEditar
    }

    override suspend fun registrarAlimentacion(
        id: String,
        alimentacion: UltimaAlimentacion,
    ): AppResult<Mascota> = AppResult.Failure(DataError.Unknown)

    override suspend fun eliminarMascota(id: String): AppResult<Unit> {
        llamadasEliminar++
        ultimoEliminado = id
        return resultadoEliminar
    }
}