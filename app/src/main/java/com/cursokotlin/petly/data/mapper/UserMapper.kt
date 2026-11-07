package com.cursokotlin.petly.data.mapper

import com.cursokotlin.petly.data.dto.LoginRequestDto
import com.cursokotlin.petly.data.dto.RegisterRequestDto
import com.cursokotlin.petly.data.dto.UserDto
import com.cursokotlin.petly.domain.model.User

fun UserDto.toDomain(): User = User(id = id, nombre = nombre, correo = correo)

/** El correo y el nombre se recortan; la contraseña nunca se toca. */
fun loginRequest(correo: String, contrasena: String) =
    LoginRequestDto(correo = correo.trim(), contrasena = contrasena)

fun registerRequest(nombre: String, correo: String, contrasena: String) =
    RegisterRequestDto(nombre = nombre.trim(), correo = correo.trim(), contrasena = contrasena)