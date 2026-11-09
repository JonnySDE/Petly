package com.cursokotlin.petly.presentations.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cursokotlin.petly.R
import com.cursokotlin.petly.presentations.components.ErrorContent
import com.cursokotlin.petly.presentations.components.LoadingContent
import com.cursokotlin.petly.presentations.components.MascotaAvatar
import com.cursokotlin.petly.presentations.components.PetlyBackground
import com.cursokotlin.petly.presentations.components.PetlyCard
import com.cursokotlin.petly.presentations.components.PetlyChip
import com.cursokotlin.petly.presentations.components.PetlyIcon
import com.cursokotlin.petly.presentations.components.PetlyOutlinedButton
import com.cursokotlin.petly.presentations.components.PetlyPrimaryButton
import com.cursokotlin.petly.presentations.components.PetlySheet
import com.cursokotlin.petly.presentations.viewmodels.AlimentacionDetalleUi
import com.cursokotlin.petly.presentations.viewmodels.DetailUiState
import com.cursokotlin.petly.presentations.viewmodels.DetailViewModel
import com.cursokotlin.petly.presentations.viewmodels.MascotaDetalleUi
import com.cursokotlin.petly.ui.theme.PetlyChipVaccinated
import com.cursokotlin.petly.ui.theme.PetlyGreenText
import com.cursokotlin.petly.ui.theme.PetlyOrange
import com.cursokotlin.petly.ui.theme.PetlyOrangeChip
import com.cursokotlin.petly.ui.theme.PetlyRedChip
import com.cursokotlin.petly.ui.theme.PetlyRedText
import com.cursokotlin.petly.ui.theme.PetlyTextSecondary
import com.cursokotlin.petly.ui.theme.PetlyTheme
import com.cursokotlin.petly.presentations.components.PetlyTopBar
import com.cursokotlin.petly.presentations.components.PetlyConfirmDialog


@Composable
fun DetailRoute(
    viewModel: DetailViewModel,
    actualizar: Boolean,
    onActualizarConsumido: () -> Unit,
    mensajeExterno: Int?,
    onMensajeExternoMostrado: () -> Unit,
    onBack: () -> Unit,
    onEditarClick: () -> Unit,
    onRegistrarComidaClick: () -> Unit,
    onEliminada: (Int) -> Unit,
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

    // Otra pantalla cambió esta mascota (por ejemplo, el formulario de edición).
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
    LaunchedEffect(uiState.eliminadaMensaje) {
        uiState.eliminadaMensaje?.let { mensajeEliminada ->
            onEliminada(mensajeEliminada)
            viewModel.onEliminadaConsumida()
        }
    }

    LaunchedEffect(uiState.sesionCerrada) {
        if (uiState.sesionCerrada) {
            onSesionCerrada()
            viewModel.onNavegacionConsumida()
        }
    }

    DetailScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onEditarClick = onEditarClick,
        onRegistrarComidaClick = onRegistrarComidaClick,
        onEliminarClick = viewModel::onEliminarClick,
        onConfirmarEliminar = viewModel::onConfirmarEliminar,
        onCancelarEliminar = viewModel::onCancelarEliminar,
        onReintentarClick = viewModel::onReintentarClick,
    )
}

@Composable
fun DetailScreen(
    uiState: DetailUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onEditarClick: () -> Unit,
    onRegistrarComidaClick: () -> Unit,
    onEliminarClick: () -> Unit,
    onConfirmarEliminar: () -> Unit,
    onCancelarEliminar: () -> Unit,
    onReintentarClick: () -> Unit,
) {
    PetlyBackground {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            PetlyTopBar(
                title = stringResource(R.string.detail_title),
                onBack = onBack,
                actionIcon = if (uiState.mascota != null) R.drawable.ic_delete else null,
                actionDescription = stringResource(R.string.action_delete),
                onAction = onEliminarClick,
            )
            PetlySheet(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val mascota = uiState.mascota
                when {
                    mascota != null -> DetailContent(
                        mascota = mascota,
                        onEditarClick = onEditarClick,
                        onRegistrarComidaClick = onRegistrarComidaClick,
                    )
                    uiState.isLoading -> LoadingContent()
                    uiState.errorMessage != null -> ErrorContent(
                        message = stringResource(uiState.errorMessage),
                        onRetry = onReintentarClick,
                    )
                    else -> Unit
                }
            }
        }
        val mascotaActual = uiState.mascota
        if (uiState.confirmarEliminar && mascotaActual != null) {
            PetlyConfirmDialog(
                title = stringResource(R.string.delete_title, mascotaActual.nombre),
                message = stringResource(R.string.delete_message),
                confirmText = stringResource(R.string.action_delete),
                onConfirm = onConfirmarEliminar,
                onDismiss = onCancelarEliminar,
                isLoading = uiState.isDeleting,
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
        )
    }
}


@Composable
private fun DetailContent(
    mascota: MascotaDetalleUi,
    onEditarClick: () -> Unit,
    onRegistrarComidaClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(20.dp),
    ) {
        PetlyCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MascotaAvatar(especie = mascota.especie, size = 72.dp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = mascota.nombre, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = "${mascota.especie} · " +
                                pluralStringResource(R.plurals.age_years, mascota.edadAnios, mascota.edadAnios),
                        style = MaterialTheme.typography.bodySmall,
                        color = PetlyTextSecondary,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (mascota.estaVacunado) {
                        PetlyChip(
                            text = stringResource(R.string.chip_vaccinated),
                            background = PetlyChipVaccinated,
                            contentColor = PetlyGreenText,
                            iconRes = R.drawable.ic_check,
                        )
                    } else {
                        PetlyChip(
                            text = stringResource(R.string.chip_not_vaccinated),
                            background = PetlyRedChip,
                            contentColor = PetlyRedText,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        SectionLabel(R.string.detail_vet_info)
        PetlyCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.detail_last_visit),
                    style = MaterialTheme.typography.bodySmall,
                    color = PetlyTextSecondary,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = mascota.ultimaVisita ?: stringResource(R.string.detail_no_visits),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        SectionLabel(R.string.detail_feeding_section)
        FeedingCard(yaComio = mascota.yaComio, alimentacion = mascota.alimentacion)

        Spacer(modifier = Modifier.height(24.dp))
        PetlyPrimaryButton(
            text = stringResource(R.string.action_register_feeding),
            onClick = onRegistrarComidaClick,
        )
        Spacer(modifier = Modifier.height(12.dp))
        PetlyOutlinedButton(
            text = stringResource(R.string.action_edit_pet),
            onClick = onEditarClick,
        )
    }
}

@Composable
private fun SectionLabel(@StringRes texto: Int) {
    Text(
        text = stringResource(texto).uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = PetlyTextSecondary,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

/** Verde si ya comió hoy; naranja si está pendiente (con el último registro, si existe). */
@Composable
private fun FeedingCard(
    yaComio: Boolean,
    alimentacion: AlimentacionDetalleUi?,
) {
    val fondo = if (yaComio) PetlyChipVaccinated else PetlyOrangeChip
    val color = if (yaComio) PetlyGreenText else PetlyOrange

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = fondo,
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PetlyIcon(
                    id = if (yaComio) R.drawable.ic_check_circle else R.drawable.ic_warning,
                    size = 24.dp,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(
                        if (yaComio) R.string.feeding_fed_today else R.string.feeding_pending_today,
                    ),
                    color = color,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (alimentacion == null) {
                Text(
                    text = stringResource(R.string.feeding_no_records),
                    style = MaterialTheme.typography.bodySmall,
                    color = PetlyTextSecondary,
                )
            } else {
                DatoLinea(R.string.feeding_type, alimentacion.tipo)
                DatoLinea(R.string.feeding_amount, alimentacion.cantidad)
                if (yaComio) {
                    DatoLinea(R.string.feeding_time, alimentacion.hora)
                } else {
                    DatoLinea(R.string.feeding_last_record, "${alimentacion.fecha}, ${alimentacion.hora}")
                }
            }
        }
    }
}

@Composable
private fun DatoLinea(@StringRes etiqueta: Int, valor: String) {
    Text(
        text = stringResource(R.string.label_value, stringResource(etiqueta), valor),
        style = MaterialTheme.typography.bodySmall,
        color = PetlyTextSecondary,
    )
}
