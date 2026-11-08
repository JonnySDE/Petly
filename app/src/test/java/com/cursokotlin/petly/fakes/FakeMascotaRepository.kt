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

    override suspend fun obtenerMascotas(): AppResult<List<Mascota>> {
        llamadasLista++
        return resultadoLista
    }

    override suspend fun obtenerMascota(id: String): AppResult<Mascota> = AppResult.Failure(DataError.Unknown)

    override suspend fun crearMascota(datos: MascotaInput): AppResult<Mascota> =
        AppResult.Failure(DataError.Unknown)

    override suspend fun editarMascota(id: String, datos: MascotaInput): AppResult<Mascota> =
        AppResult.Failure(DataError.Unknown)

    override suspend fun registrarAlimentacion(
        id: String,
        alimentacion: UltimaAlimentacion,
    ): AppResult<Mascota> = AppResult.Failure(DataError.Unknown)

    override suspend fun eliminarMascota(id: String): AppResult<Unit> = AppResult.Failure(DataError.Unknown)
}