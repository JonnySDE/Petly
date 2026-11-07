package com.cursokotlin.petly.data.datasource

import com.cursokotlin.petly.data.dto.LoginRequestDto
import com.cursokotlin.petly.data.dto.RegisterRequestDto
import com.cursokotlin.petly.data.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    /** Devuelve el usuario en el cuerpo; no trae token. */
    @POST("register")
    suspend fun register(@Body body: RegisterRequestDto): UserDto

    /**
     * El token viaja en el ENCABEZADO de la respuesta, por eso este es el único
     * método de la app que devuelve Response<T>: hace falta leer los headers.
     */
    @POST("login")
    suspend fun login(@Body body: LoginRequestDto): Response<UserDto>
}