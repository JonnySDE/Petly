package com.cursokotlin.petly.core.network

import com.cursokotlin.petly.data.local.SessionStorage
import okhttp3.Interceptor
import okhttp3.Response

/** Agrega Authorization: Bearer <token> a cada petición, en un solo lugar. */
class AuthInterceptor(
    private val sessionStorage: SessionStorage,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionStorage.obtenerToken()
        val peticion = if (token.isNullOrBlank()) {
            chain.request()
        } else {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }
        return chain.proceed(peticion)
    }
}