package com.cursokotlin.petly.core.di

import android.content.Context
import com.cursokotlin.petly.BuildConfig
import com.cursokotlin.petly.core.network.AuthInterceptor
import com.cursokotlin.petly.data.datasource.AuthApi
import com.cursokotlin.petly.data.local.SessionStorage
import com.cursokotlin.petly.data.local.SessionStorageImpl
import com.cursokotlin.petly.data.repository.AuthRepositoryImpl
import com.cursokotlin.petly.domain.repository.AuthRepository
import java.time.Clock
import java.util.concurrent.TimeUnit
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cursokotlin.petly.presentations.viewmodels.LoginViewModel
import com.cursokotlin.petly.presentations.viewmodels.RegisterViewModel

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

    private val authApi: AuthApi by lazy { retrofit.create<AuthApi>() }

    val authRepository: AuthRepository by lazy { AuthRepositoryImpl(authApi, sessionStorage) }

    val loginViewModelFactory: ViewModelProvider.Factory = viewModelFactory {
        initializer { LoginViewModel(authRepository) }
    }

    val registerViewModelFactory: ViewModelProvider.Factory = viewModelFactory {
        initializer { RegisterViewModel(authRepository) }
    }

    private companion object {
        const val PREFS_SESION = "petly_session"
    }
}