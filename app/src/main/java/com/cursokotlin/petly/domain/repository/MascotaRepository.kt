package com.cursokotlin.petly.domain.repository

import com.cursokotlin.petly.domain.model.AppResult
import com.cursokotlin.petly.domain.model.Mascota
import com.cursokotlin.petly.domain.model.MascotaInput
import com.cursokotlin.petly.domain.model.UltimaAlimentacion

interface MascotaRepository {
    suspend fun obtenerMascotas(): AppResult<List<Mascota>>

    suspend fun obtenerMascota(id: String): AppResult<Mascota>

    suspend fun crearMascota(datos: MascotaInput): AppResult<Mascota>

    /** Edita los datos generales; no toca la alimentación. */
    suspend fun editarMascota(id: String, datos: MascotaInput): AppResult<Mascota>

    suspend fun registrarAlimentacion(id: String, alimentacion: UltimaAlimentacion): AppResult<Mascota>

    suspend fun eliminarMascota(id: String): AppResult<Unit>
}