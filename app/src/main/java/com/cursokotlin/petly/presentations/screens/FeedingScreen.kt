package com.cursokotlin.petly.presentations.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cursokotlin.petly.R
import com.cursokotlin.petly.presentations.components.ErrorContent
import com.cursokotlin.petly.presentations.components.LoadingContent
import com.cursokotlin.petly.presentations.components.MascotaAvatar
import com.cursokotlin.petly.presentations.components.PetlyBackground
import com.cursokotlin.petly.presentations.components.PetlyCard
import com.cursokotlin.petly.presentations.components.PetlyIcon
import com.cursokotlin.petly.presentations.components.PetlyPrimaryButton
import com.cursokotlin.petly.presentations.components.PetlySheet
import com.cursokotlin.petly.presentations.components.PetlyTextField
import com.cursokotlin.petly.presentations.components.PetlyTopBar
import com.cursokotlin.petly.presentations.viewmodels.FeedingUiState
import com.cursokotlin.petly.presentations.viewmodels.FeedingViewModel
import com.cursokotlin.petly.ui.theme.PetlyTextSecondary
import com.cursokotlin.petly.ui.theme.PetlyTheme

@Composable
fun FeedingRoute(
    viewModel: FeedingViewModel,
    onBack: () -> Unit,
    onGuardado: (Int) -> Unit,
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

    LaunchedEffect(uiState.guardadoMensaje) {
        uiState.guardadoMensaje?.let { guardado ->
            onGuardado(guardado)
            viewModel.onGuardadoConsumido()
        }
    }

    LaunchedEffect(uiState.sesionCerrada) {
        if (uiState.sesionCerrada) {
            onSesionCerrada()
            viewModel.onNavegacionConsumida()
        }
    }

    FeedingScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onTipoChange = viewModel::onTipoChange,
        onCantidadChange = viewModel::onCantidadChange,
        onGuardarClick = viewModel::onGuardarClick,
        onReintentarClick = viewModel::onReintentarCargaClick,
    )
}

@Composable
fun FeedingScreen(
    uiState: FeedingUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onTipoChange: (String) -> Unit,
    onCantidadChange: (String) -> Unit,
    onGuardarClick: () -> Unit,
    onReintentarClick: () -> Unit,
) {
    PetlyBackground {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            PetlyTopBar(title = stringResource(R.string.feeding_title), onBack = onBack)
            PetlySheet(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when {
                    uiState.isLoadingInicial -> LoadingContent()
                    uiState.errorCarga != null -> ErrorContent(
                        message = stringResource(uiState.errorCarga),
                        onRetry = onReintentarClick,
                    )
                    else -> FeedingForm(
                        uiState = uiState,
                        onTipoChange = onTipoChange,
                        onCantidadChange = onCantidadChange,
                        onGuardarClick = onGuardarClick,
                    )
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .imePadding()
                .padding(16.dp),
        )
    }
}

@Composable
private fun FeedingForm(
    uiState: FeedingUiState,
    onTipoChange: (String) -> Unit,
    onCantidadChange: (String) -> Unit,
    onGuardarClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .imePadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PetlyCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MascotaAvatar(especie = uiState.especie, size = 56.dp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = uiState.nombreMascota, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${uiState.especie} · ${stringResource(R.string.feeding_control_daily)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PetlyTextSecondary,
                    )
                }
            }
        }

        PetlyTextField(
            label = stringResource(R.string.label_food_type),
            value = uiState.tipoAlimento,
            onValueChange = onTipoChange,
            errorMessage = uiState.tipoError?.let { stringResource(it) },
            enabled = !uiState.isSaving,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        PetlyTextField(
            label = stringResource(R.string.label_portion),
            value = uiState.cantidad,
            onValueChange = onCantidadChange,
            errorMessage = uiState.cantidadError?.let { stringResource(it) },
            enabled = !uiState.isSaving,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onGuardarClick() }),
        )

        PetlyTextField(
            label = stringResource(R.string.label_record_time),
            value = stringResource(R.string.feeding_time_auto, uiState.horaTexto),
            onValueChange = {},
            readOnly = true,
            enabled = false,
            trailingIcon = { PetlyIcon(id = R.drawable.ic_clock) },
        )

        Spacer(modifier = Modifier.height(8.dp))
        PetlyPrimaryButton(
            text = stringResource(R.string.action_confirm_feeding),
            onClick = onGuardarClick,
            loading = uiState.isSaving,
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun FeedingScreenPreview() {
    PetlyTheme {
        FeedingScreen(
            uiState = FeedingUiState(
                isLoadingInicial = false,
                nombreMascota = "Chester",
                especie = "Perro",
                tipoAlimento = "Croquetas",
                cantidad = "250 g",
                horaTexto = "8:30 a. m.",
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onTipoChange = {},
            onCantidadChange = {},
            onGuardarClick = {},
            onReintentarClick = {},
        )
    }
}