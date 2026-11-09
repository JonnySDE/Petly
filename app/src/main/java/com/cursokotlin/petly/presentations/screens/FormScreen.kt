package com.cursokotlin.petly.presentations.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cursokotlin.petly.R
import com.cursokotlin.petly.presentations.components.ErrorContent
import com.cursokotlin.petly.presentations.components.LoadingContent
import com.cursokotlin.petly.presentations.components.MascotaAvatar
import com.cursokotlin.petly.presentations.components.PetlyBackground
import com.cursokotlin.petly.presentations.components.PetlyCard
import com.cursokotlin.petly.presentations.components.PetlyDropdownField
import com.cursokotlin.petly.presentations.components.PetlyIcon
import com.cursokotlin.petly.presentations.components.PetlyOutlinedButton
import com.cursokotlin.petly.presentations.components.PetlyPickerField
import com.cursokotlin.petly.presentations.components.PetlyPrimaryButton
import com.cursokotlin.petly.presentations.components.PetlySheet
import com.cursokotlin.petly.presentations.components.PetlyTextField
import com.cursokotlin.petly.presentations.components.PetlyTopBar
import com.cursokotlin.petly.presentations.viewmodels.FormUiState
import com.cursokotlin.petly.presentations.viewmodels.FormViewModel
import com.cursokotlin.petly.ui.theme.PetlyBorder
import com.cursokotlin.petly.ui.theme.PetlyInputBackground
import com.cursokotlin.petly.ui.theme.PetlyTealDark
import com.cursokotlin.petly.ui.theme.PetlyTextPrimary
import com.cursokotlin.petly.ui.theme.PetlyTheme

/** Conecta el ViewModel con la pantalla y reacciona a mensajes y navegación. */
@Composable
fun FormRoute(
    viewModel: FormViewModel,
    onBack: () -> Unit,
    onGuardado: (mensaje: Int) -> Unit,
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
        uiState.guardadoMensaje?.let { mensajeGuardado ->
            onGuardado(mensajeGuardado)
            viewModel.onGuardadoConsumido()
        }
    }

    LaunchedEffect(uiState.sesionCerrada) {
        if (uiState.sesionCerrada) {
            onSesionCerrada()
            viewModel.onNavegacionConsumida()
        }
    }

    FormScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onNombreChange = viewModel::onNombreChange,
        onEspecieChange = viewModel::onEspecieChange,
        onEdadChange = viewModel::onEdadChange,
        onVacunadoChange = viewModel::onVacunadoChange,
        onFechaVisitaSeleccionada = viewModel::onFechaVisitaSeleccionada,
        onQuitarFechaClick = viewModel::onQuitarFechaClick,
        onGuardarClick = viewModel::onGuardarClick,
        onReintentarCargaClick = viewModel::onReintentarCargaClick,
    )
}

@Composable
fun FormScreen(
    uiState: FormUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onNombreChange: (String) -> Unit,
    onEspecieChange: (String) -> Unit,
    onEdadChange: (String) -> Unit,
    onVacunadoChange: (Boolean) -> Unit,
    onFechaVisitaSeleccionada: (Long?) -> Unit,
    onQuitarFechaClick: () -> Unit,
    onGuardarClick: () -> Unit,
    onReintentarCargaClick: () -> Unit,
) {
    PetlyBackground {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            PetlyTopBar(
                title = stringResource(
                    if (uiState.modoEdicion) R.string.form_title_edit else R.string.form_title_new,
                ),
                onBack = onBack,
            )
            PetlySheet(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when {
                    uiState.isLoadingInicial -> LoadingContent()
                    uiState.errorCarga != null -> ErrorContent(
                        message = stringResource(uiState.errorCarga),
                        onRetry = onReintentarCargaClick,
                    )
                    else -> FormContent(
                        uiState = uiState,
                        onBack = onBack,
                        onNombreChange = onNombreChange,
                        onEspecieChange = onEspecieChange,
                        onEdadChange = onEdadChange,
                        onVacunadoChange = onVacunadoChange,
                        onFechaVisitaSeleccionada = onFechaVisitaSeleccionada,
                        onQuitarFechaClick = onQuitarFechaClick,
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
private fun FormContent(
    uiState: FormUiState,
    onBack: () -> Unit,
    onNombreChange: (String) -> Unit,
    onEspecieChange: (String) -> Unit,
    onEdadChange: (String) -> Unit,
    onVacunadoChange: (Boolean) -> Unit,
    onFechaVisitaSeleccionada: (Long?) -> Unit,
    onQuitarFechaClick: () -> Unit,
    onGuardarClick: () -> Unit,
) {
    // Solo indica si el selector de fechas está abierto: es estado visual, no de negocio.
    var mostrarSelector by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .imePadding()
            .padding(20.dp),
    ) {
        PetlyCard(modifier = Modifier.fillMaxWidth()) {
            // El avatar cambia con la especie elegida.
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                MascotaAvatar(especie = uiState.especie, size = 80.dp)
            }
            Spacer(modifier = Modifier.height(16.dp))

            PetlyTextField(
                label = stringResource(R.string.label_pet_name),
                value = uiState.nombre,
                onValueChange = onNombreChange,
                errorMessage = uiState.nombreError?.let { stringResource(it) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                ),
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                PetlyDropdownField(
                    label = stringResource(R.string.label_species),
                    value = uiState.especie,
                    options = uiState.especies,
                    onOptionSelected = onEspecieChange,
                    modifier = Modifier.weight(1f),
                    errorMessage = uiState.especieError?.let { stringResource(it) },
                )
                PetlyTextField(
                    label = stringResource(R.string.label_age),
                    value = uiState.edad,
                    onValueChange = onEdadChange,
                    modifier = Modifier.weight(1f),
                    errorMessage = uiState.edadError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.form_vaccination_card).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = PetlyTextPrimary,
            )
            Spacer(modifier = Modifier.height(6.dp))
            VaccinationSwitch(vacunado = uiState.estaVacunado, onChange = onVacunadoChange)
            Spacer(modifier = Modifier.height(12.dp))

            PetlyPickerField(
                label = stringResource(R.string.label_last_visit),
                value = uiState.fechaVisitaTexto,
                placeholder = stringResource(R.string.form_no_date),
                onClick = { mostrarSelector = true },
                iconRes = R.drawable.ic_calendar,
                errorMessage = uiState.fechaError?.let { stringResource(it) },
            )
            if (uiState.fechaVisita != null) {
                TextButton(onClick = onQuitarFechaClick) {
                    Text(text = stringResource(R.string.form_remove_date))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            PetlyPrimaryButton(
                text = stringResource(
                    if (uiState.modoEdicion) R.string.action_update_changes else R.string.action_save_pet,
                ),
                onClick = onGuardarClick,
                loading = uiState.isSaving,
            )
            if (uiState.modoEdicion) {
                Spacer(modifier = Modifier.height(12.dp))
                PetlyOutlinedButton(
                    text = stringResource(R.string.action_cancel),
                    onClick = onBack,
                    enabled = !uiState.isSaving,
                )
            }
        }
    }

    if (mostrarSelector) {
        FechaVisitaDialog(
            fechaInicialMillis = uiState.fechaVisitaMillis,
            onConfirmar = { millis ->
                onFechaVisitaSeleccionada(millis)
                mostrarSelector = false
            },
            onCancelar = { mostrarSelector = false },
        )
    }
}

@Composable
private fun VaccinationSwitch(
    vacunado: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = PetlyInputBackground,
        border = BorderStroke(1.dp, PetlyBorder),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (vacunado) {
                PetlyIcon(id = R.drawable.ic_check, size = 16.dp)
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = stringResource(
                    if (vacunado) R.string.form_vaccinated_on else R.string.form_vaccinated_off,
                ),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = vacunado,
                onCheckedChange = onChange,
                colors = SwitchDefaults.colors(checkedTrackColor = PetlyTealDark),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FechaVisitaDialog(
    fechaInicialMillis: Long?,
    onConfirmar: (Long?) -> Unit,
    onCancelar: () -> Unit,
) {
    val estado = rememberDatePickerState(initialSelectedDateMillis = fechaInicialMillis)
    DatePickerDialog(
        onDismissRequest = onCancelar,
        confirmButton = {
            TextButton(onClick = { onConfirmar(estado.selectedDateMillis) }) {
                Text(text = stringResource(R.string.action_accept))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text(text = stringResource(R.string.action_cancel))
            }
        },
    ) {
        DatePicker(state = estado)
    }
}

@Preview(showSystemUi = true)
@Composable
private fun FormScreenNuevaPreview() {
    PetlyTheme {
        FormScreen(
            uiState = FormUiState(nombre = "Bob", edad = "4", nombreError = null, edadError = R.string.error_age_range),
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onNombreChange = {},
            onEspecieChange = {},
            onEdadChange = {},
            onVacunadoChange = {},
            onFechaVisitaSeleccionada = {},
            onQuitarFechaClick = {},
            onGuardarClick = {},
            onReintentarCargaClick = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun FormScreenEdicionPreview() {
    PetlyTheme {
        FormScreen(
            uiState = FormUiState(
                modoEdicion = true,
                nombre = "Luna",
                especie = "Gato",
                edad = "3",
                estaVacunado = true,
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onNombreChange = {},
            onEspecieChange = {},
            onEdadChange = {},
            onVacunadoChange = {},
            onFechaVisitaSeleccionada = {},
            onQuitarFechaClick = {},
            onGuardarClick = {},
            onReintentarCargaClick = {},
        )
    }
}