package com.cursokotlin.petly.core.di

import android.content.Context
import com.cursokotlin.petly.BuildConfig
import com.cursokotlin.petly.core.network.AuthInterceptor
import com.cursokotlin.petly.data.local.SessionStorage
import com.cursokotlin.petly.data.local.SessionStorageImpl
import java.time.Clock
import java.util.concurrent.TimeUnit
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory


@OptIn(ExperimentalSerializationApi::class)
class AppContainer(context: Context) {

    private val appContext: Context = context.applicationContext

    private val json: Json = Json {
        ignoreUnknownKeys = true // si la API agrega campos, la app no se rompe
        explicitNulls = false // un campo nulo no se envía y uno ausente llega como null
    }

    val clock: Clock = Clock.systemDefaultZone()

    val sessionStorage: SessionStorage by lazy {
        SessionStorageImpl(appContext.getSharedPreferences(PREFS_SESION, Context.MODE_PRIVATE))
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            // Primero la autenticación y después el logging, para ver la petición final.
            .addInterceptor(AuthInterceptor(sessionStorage))
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                            redactHeader("Authorization")
                        },
                    )
                }
            }
            .build()
    }

    // El único Retrofit de la app.
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    private companion object {
        const val PREFS_SESION = "petly_session"
    }
}