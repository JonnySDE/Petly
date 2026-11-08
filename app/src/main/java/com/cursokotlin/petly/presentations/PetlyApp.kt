package com.cursokotlin.petly.presentations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cursokotlin.petly.core.di.AppContainer
import com.cursokotlin.petly.presentations.screens.HomeTemporalScreen
import com.cursokotlin.petly.presentations.screens.LoginRoute
import com.cursokotlin.petly.presentations.screens.RegisterRoute
import com.cursokotlin.petly.presentations.viewmodels.LoginViewModel
import com.cursokotlin.petly.presentations.viewmodels.RegisterViewModel

private enum class PantallaTemporal { Login, Register, Home }

/** Composición raíz de la app. Temporal: en el commit 7 el cuerpo pasa a ser el NavHost. */
@Composable
fun PetlyApp(container: AppContainer) {
    var pantalla by rememberSaveable {
        mutableStateOf(
            if (container.authRepository.haySesion()) PantallaTemporal.Home else PantallaTemporal.Login,
        )
    }
    var registroExitoso by rememberSaveable { mutableStateOf(false) }

    when (pantalla) {
        PantallaTemporal.Login -> {
            val viewModel: LoginViewModel = viewModel(factory = container.loginViewModelFactory)
            LoginRoute(
                viewModel = viewModel,
                registroExitoso = registroExitoso,
                onRegistroExitosoMostrado = { registroExitoso = false },
                onLoginSuccess = { pantalla = PantallaTemporal.Home },
                onNavigateToRegister = { pantalla = PantallaTemporal.Register },
            )
        }
        PantallaTemporal.Register -> {
            val viewModel: RegisterViewModel = viewModel(factory = container.registerViewModelFactory)
            RegisterRoute(
                viewModel = viewModel,
                onRegistered = {
                    registroExitoso = true
                    pantalla = PantallaTemporal.Login
                },
                onNavigateToLogin = { pantalla = PantallaTemporal.Login },
            )
        }
        PantallaTemporal.Home -> HomeTemporalScreen(
            nombre = container.authRepository.nombreUsuario(),
            onCerrarSesion = {
                container.authRepository.cerrarSesion()
                pantalla = PantallaTemporal.Login
            },
        )
    }
}