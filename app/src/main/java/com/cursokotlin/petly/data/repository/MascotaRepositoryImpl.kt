package com.cursokotlin.petly.data.repository

import com.cursokotlin.petly.core.network.safeApiCall
import com.cursokotlin.petly.data.datasource.MascotaApi
import com.cursokotlin.petly.data.mapper.toDomain
import com.cursokotlin.petly.data.mapper.toRequestDto
import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.model.Mascota
import com.cursokotlin.petly.domain.model.MascotaInput
import com.cursokotlin.petly.domain.model.UltimaAlimentacion
import com.cursokotlin.petly.domain.repository.MascotaRepository

class MascotaRepositoryImpl(
    private val api: MascotaApi,
) : MascotaRepository {

    override suspend fun obtenerMascotas(): AppResult<List<Mascota>> = safeApiCall {
        api.obtenerMascotas().map { it.toDomain() }
    }

    override suspend fun obtenerMascota(id: String): AppResult<Mascota> = safeApiCall {
        api.obtenerMascota(id).toDomain()
    }

    override suspend fun crearMascota(datos: MascotaInput): AppResult<Mascota> = safeApiCall {
        api.crearMascota(datos.toRequestDto()).toDomain()
    }

    override suspend fun editarMascota(id: String, datos: MascotaInput): AppResult<Mascota> = safeApiCall {
        api.editarMascota(id, datos.toRequestDto()).toDomain()
    }

    override suspend fun registrarAlimentacion(
        id: String,
        alimentacion: UltimaAlimentacion,
    ): AppResult<Mascota> = safeApiCall {
        api.registrarAlimentacion(id, alimentacion.toRequestDto()).toDomain()
    }

    override suspend fun eliminarMascota(id: String): AppResult<Unit> = safeApiCall {
        api.eliminarMascota(id)
    }
}