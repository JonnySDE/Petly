package com.cursokotlin.petly.data.datasource

import com.cursokotlin.petly.data.dto.AlimentacionRequestDto
import com.cursokotlin.petly.data.dto.MascotaDto
import com.cursokotlin.petly.data.dto.MascotaRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/** Todos los métodos usan el retorno por defecto de Retrofit: el token lo agrega el interceptor. */
interface MascotaApi {

    @GET("mascotas")
    suspend fun obtenerMascotas(): List<MascotaDto>

    @GET("mascotas/{id}")
    suspend fun obtenerMascota(@Path("id") id: String): MascotaDto

    @POST("mascotas")
    suspend fun crearMascota(@Body body: MascotaRequestDto): MascotaDto

    /** Reemplaza los datos generales; no toca la alimentación. */
    @PUT("mascotas/{id}")
    suspend fun editarMascota(@Path("id") id: String, @Body body: MascotaRequestDto): MascotaDto

    /** Registra la última alimentación. */
    @PATCH("mascotas/{id}")
    suspend fun registrarAlimentacion(
        @Path("id") id: String,
        @Body body: AlimentacionRequestDto,
    ): MascotaDto

    /** La API responde 204 sin cuerpo; Retrofit lo acepta con Unit. */
    @DELETE("mascotas/{id}")
    suspend fun eliminarMascota(@Path("id") id: String)
}