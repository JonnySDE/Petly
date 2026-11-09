package com.cursokotlin.petly.presentations.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cursokotlin.petly.core.di.AppContainer
import com.cursokotlin.petly.presentations.screens.DetailRoute
import com.cursokotlin.petly.presentations.screens.FormRoute
import com.cursokotlin.petly.presentations.screens.HomeRoute
import com.cursokotlin.petly.presentations.screens.LoginRoute
import com.cursokotlin.petly.presentations.screens.RegisterRoute
import com.cursokotlin.petly.presentations.viewmodels.DetailViewModel
import com.cursokotlin.petly.presentations.viewmodels.FormViewModel
import com.cursokotlin.petly.presentations.viewmodels.HomeViewModel
import com.cursokotlin.petly.presentations.viewmodels.LoginViewModel
import com.cursokotlin.petly.presentations.viewmodels.RegisterViewModel

/** Clave con la que el registro le avisa al login que la cuenta se creó. */
private const val KEY_REGISTRO_EXITOSO = "registro_exitoso"

/** Avisos entre pantallas: "los datos cambiaron, vuelve a pedirlos" y "muestra este mensaje". */
private const val KEY_ACTUALIZAR = "actualizar"
private const val KEY_MENSAJE = "mensaje"

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

        composable(Routes.HOME) { entrada ->
            val viewModel: HomeViewModel = viewModel(factory = container.homeViewModelFactory)
            val actualizar by entrada.savedStateHandle
                .getStateFlow(KEY_ACTUALIZAR, false)
                .collectAsStateWithLifecycle()
            val mensajeExterno by entrada.savedStateHandle
                .getStateFlow<Int?>(KEY_MENSAJE, null)
                .collectAsStateWithLifecycle()

            HomeRoute(
                viewModel = viewModel,
                actualizar = actualizar,
                onActualizarConsumido = { entrada.savedStateHandle[KEY_ACTUALIZAR] = false },
                mensajeExterno = mensajeExterno,
                onMensajeExternoMostrado = { entrada.savedStateHandle[KEY_MENSAJE] = null },
                onMascotaClick = { id -> navController.navigate(Routes.detail(id)) },
                onAgregarClick = { navController.navigate(Routes.form()) },
                onSesionCerrada = { navController.navegarLimpiandoPila(Routes.LOGIN) },
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument(Routes.ARG_ID) { type = NavType.StringType }),
        ) { entrada ->
            val id = entrada.arguments?.getString(Routes.ARG_ID).orEmpty()
            val viewModel: DetailViewModel = viewModel(factory = container.detailViewModelFactory(id))
            val actualizar by entrada.savedStateHandle
                .getStateFlow(KEY_ACTUALIZAR, false)
                .collectAsStateWithLifecycle()
            val mensajeExterno by entrada.savedStateHandle
                .getStateFlow<Int?>(KEY_MENSAJE, null)
                .collectAsStateWithLifecycle()

            DetailRoute(
                viewModel = viewModel,
                actualizar = actualizar,
                onActualizarConsumido = { entrada.savedStateHandle[KEY_ACTUALIZAR] = false },
                mensajeExterno = mensajeExterno,
                onMensajeExternoMostrado = { entrada.savedStateHandle[KEY_MENSAJE] = null },
                onBack = { navController.popBackStack() },
                onEditarClick = { navController.navigate(Routes.form(id)) },
                onRegistrarComidaClick = { /* commit 13: registro de alimentación */ },
                onEliminarClick = { /* commit 12: eliminar con confirmación */ },
                onSesionCerrada = { navController.navegarLimpiandoPila(Routes.LOGIN) },
            )
        }

        composable(
            route = Routes.FORM,
            arguments = listOf(
                navArgument(Routes.ARG_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) { entrada ->
            val id = entrada.arguments?.getString(Routes.ARG_ID)
            val viewModel: FormViewModel = viewModel(factory = container.formViewModelFactory(id))

            FormRoute(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onGuardado = { mensaje ->
                    navController.avisarCambio(mensaje)
                    navController.popBackStack()
                },
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

private fun NavHostController.avisarCambio(@StringRes mensaje: Int) {
    getBackStackEntry(Routes.HOME).savedStateHandle[KEY_ACTUALIZAR] = true
    previousBackStackEntry?.savedStateHandle?.let {
        it[KEY_ACTUALIZAR] = true
        it[KEY_MENSAJE] = mensaje
    }
}