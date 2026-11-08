package com.cursokotlin.petly.presentations.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cursokotlin.petly.core.di.AppContainer
import com.cursokotlin.petly.presentations.screens.HomeRoute
import com.cursokotlin.petly.presentations.screens.LoginRoute
import com.cursokotlin.petly.presentations.screens.RegisterRoute
import com.cursokotlin.petly.presentations.viewmodels.HomeViewModel
import com.cursokotlin.petly.presentations.viewmodels.LoginViewModel
import com.cursokotlin.petly.presentations.viewmodels.RegisterViewModel

private const val KEY_REGISTRO_EXITOSO = "registro_exitoso"

@Composable
fun PetlyNavHost(
    container: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    // Si hay sesión guardada se omite el login (RF-03).
    val destinoInicial = remember {
        if (container.authRepository.haySesion()) Routes.HOME else Routes.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = destinoInicial,
        modifier = modifier,
    ) {
        composable(Routes.LOGIN) { entrada ->
            val viewModel: LoginViewModel = viewModel(factory = container.loginViewModelFactory)
            val registroExitoso by entrada.savedStateHandle
                .getStateFlow(KEY_REGISTRO_EXITOSO, false)
                .collectAsStateWithLifecycle()

            LoginRoute(
                viewModel = viewModel,
                registroExitoso = registroExitoso,
                onRegistroExitosoMostrado = { entrada.savedStateHandle[KEY_REGISTRO_EXITOSO] = false },
                onLoginSuccess = { navController.navegarLimpiandoPila(Routes.HOME) },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
            )
        }

        composable(Routes.REGISTER) {
            val viewModel: RegisterViewModel = viewModel(factory = container.registerViewModelFactory)

            RegisterRoute(
                viewModel = viewModel,
                onRegistered = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(KEY_REGISTRO_EXITOSO, true)
                    navController.popBackStack()
                },
                onNavigateToLogin = { navController.popBackStack() },
            )
        }

        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel(factory = container.homeViewModelFactory)

            HomeRoute(
                viewModel = viewModel,
                onMascotaClick = { /* commit 10: ficha de detalle */ },
                onAgregarClick = { /* commit 11: formulario */ },
                onSesionCerrada = { navController.navegarLimpiandoPila(Routes.LOGIN) },
            )
        }
    }
}

private fun NavHostController.navegarLimpiandoPila(destino: String) {
    navigate(destino) {
        popUpTo(graph.id) { inclusive = true }
    }
}