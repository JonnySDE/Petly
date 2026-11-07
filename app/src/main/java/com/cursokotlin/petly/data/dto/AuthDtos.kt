package com.cursokotlin.petly.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val nombre: String,
    val correo: String,
    val contrasena: String,
)

@Serializable
data class LoginRequestDto(
    val correo: String,
    val contrasena: String,
)

@Serializable
data class UserDto(
    val id: String,
    val nombre: String,
    val correo: String,
)