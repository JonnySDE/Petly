package com.cursokotlin.petly.presentations.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cursokotlin.petly.R
import com.cursokotlin.petly.presentations.components.EmptyContent
import com.cursokotlin.petly.presentations.components.ErrorContent
import com.cursokotlin.petly.presentations.components.LoadingContent
import com.cursokotlin.petly.presentations.components.MascotaCard
import com.cursokotlin.petly.presentations.components.PetlyBackground
import com.cursokotlin.petly.presentations.components.PetlyIcon
import com.cursokotlin.petly.presentations.components.PetlySheet
import com.cursokotlin.petly.presentations.viewmodels.HomeUiState
import com.cursokotlin.petly.presentations.viewmodels.HomeViewModel
import com.cursokotlin.petly.presentations.viewmodels.MascotaItemUi
import com.cursokotlin.petly.ui.theme.PetlyOrange
import com.cursokotlin.petly.ui.theme.PetlyTheme

/** Conecta el ViewModel con la pantalla y reacciona a mensajes y navegación. */
@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    actualizar: Boolean,
    onActualizarConsumido: () -> Unit,
    mensajeExterno: Int?,
    onMensajeExternoMostrado: () -> Unit,
    onMascotaClick: (String) -> Unit,
    onAgregarClick: () -> Unit,
    onSesionCerrada: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val mensaje = uiState.mensaje?.let { stringResource(it) }
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            snackbarHostState.showSnackbar(mensaje)
            viewModel.onMensajeMostrado()
        }
    }

    // Otra pantalla cambió la lista (se creó, editó o eliminó una mascota).
    LaunchedEffect(actualizar) {
        if (actualizar) {
            viewModel.onActualizar()
            onActualizarConsumido()
        }
    }

    val textoExterno = mensajeExterno?.let { stringResource(it) }
    LaunchedEffect(textoExterno) {
        if (textoExterno != null) {
            snackbarHostState.showSnackbar(textoExterno)
            onMensajeExternoMostrado()
        }
    }

    LaunchedEffect(uiState.sesionCerrada) {
        if (uiState.sesionCerrada) {
            onSesionCerrada()
            viewModel.onNavegacionConsumida()
        }
    }

    HomeScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onMascotaClick = onMascotaClick,
        onAgregarClick = onAgregarClick,
        onReintentarClick = viewModel::onReintentarClick,
        onCerrarSesionClick = viewModel::onCerrarSesionClick,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    snackbarHostState: SnackbarHostState,
    onMascotaClick: (String) -> Unit,
    onAgregarClick: () -> Unit,
    onReintentarClick: () -> Unit,
    onCerrarSesionClick: () -> Unit,
) {
    PetlyBackground {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            HomeHeader(
                iniciales = uiState.iniciales,
                nombre = uiState.nombreUsuario,
                onCerrarSesionClick = onCerrarSesionClick,
            )
            PetlySheet(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.home_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp),
                )
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when {
                        uiState.isLoading && uiState.mascotas.isEmpty() -> LoadingContent()
                        uiState.errorMessage != null -> ErrorContent(
                            message = stringResource(uiState.errorMessage),
                            onRetry = onReintentarClick,
                        )
                        uiState.mascotas.isEmpty() -> EmptyContent(
                            title = stringResource(R.string.home_empty_title),
                            subtitle = stringResource(R.string.home_empty_subtitle),
                        )
                        else -> LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 112.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(items = uiState.mascotas, key = { it.id }) { mascota ->
                                MascotaCard(
                                    mascota = mascota,
                                    onClick = { onMascotaClick(mascota.id) },
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAgregarClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(20.dp),
            shape = CircleShape,
            containerColor = PetlyOrange,
            contentColor = Color.White,
        ) {
            PetlyIcon(
                id = R.drawable.ic_add,
                contentDescription = stringResource(R.string.action_add_pet),
                size = 24.dp,
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, bottom = 96.dp),
        )
    }
}

@Composable
private fun HomeHeader(
    iniciales: String,
    nombre: String,
    onCerrarSesionClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
                .border(1.5.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = iniciales,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.home_greeting),
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = nombre,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        // Si tu ícono de salir es de colores, quita el tint para conservarlos.
        IconButton(
            onClick = onCerrarSesionClick,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.15f)),
        ) {
            PetlyIcon(
                id = R.drawable.ic_logout,
                contentDescription = stringResource(R.string.action_logout),
                tint = Color.White,
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    PetlyTheme {
        HomeScreen(
            uiState = HomeUiState(
                nombreUsuario = "Jonathan Rodriguez",
                iniciales = "JR",
                mascotas = listOf(
                    MascotaItemUi("1", "Chester", "Perro", 4, true, true, "Croquetas (250 g) • 8:30 a. m."),
                    MascotaItemUi("2", "Chispa", "Gato", 2, false, false, null),
                ),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onMascotaClick = {},
            onAgregarClick = {},
            onReintentarClick = {},
            onCerrarSesionClick = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenVaciaPreview() {
    PetlyTheme {
        HomeScreen(
            uiState = HomeUiState(nombreUsuario = "Ana", iniciales = "A"),
            snackbarHostState = remember { SnackbarHostState() },
            onMascotaClick = {},
            onAgregarClick = {},
            onReintentarClick = {},
            onCerrarSesionClick = {},
        )
    }
}