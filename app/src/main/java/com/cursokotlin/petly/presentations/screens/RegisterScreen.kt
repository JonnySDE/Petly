package com.cursokotlin.petly.presentations.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cursokotlin.petly.R
import com.cursokotlin.petly.presentations.components.AuthFooterLink
import com.cursokotlin.petly.presentations.components.AuthHeader
import com.cursokotlin.petly.presentations.components.PetlyBackground
import com.cursokotlin.petly.presentations.components.PetlyCard
import com.cursokotlin.petly.presentations.components.PetlyIcon
import com.cursokotlin.petly.presentations.components.PetlyPasswordField
import com.cursokotlin.petly.presentations.components.PetlyPrimaryButton
import com.cursokotlin.petly.presentations.components.PetlySheet
import com.cursokotlin.petly.presentations.components.PetlyTextField
import com.cursokotlin.petly.presentations.viewmodels.RegisterUiState
import com.cursokotlin.petly.presentations.viewmodels.RegisterViewModel
import com.cursokotlin.petly.ui.theme.PetlyTheme

@Composable
fun RegisterRoute(
    viewModel: RegisterViewModel,
    onRegistered: () -> Unit,
    onNavigateToLogin: () -> Unit,
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

    LaunchedEffect(uiState.registroExitoso) {
        if (uiState.registroExitoso) {
            onRegistered()
            viewModel.onNavegacionConsumida()
        }
    }

    RegisterScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNombreChange = viewModel::onNombreChange,
        onCorreoChange = viewModel::onCorreoChange,
        onContrasenaChange = viewModel::onContrasenaChange,
        onConfirmacionChange = viewModel::onConfirmacionChange,
        onRegisterClick = viewModel::onRegisterClick,
        onNavigateToLogin = onNavigateToLogin,
    )
}

@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    snackbarHostState: SnackbarHostState,
    onNombreChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onContrasenaChange: (String) -> Unit,
    onConfirmacionChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    PetlyBackground {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            AuthHeader(
                title = stringResource(R.string.register_title),
                subtitle = stringResource(R.string.register_subtitle),
                logoSize = 72.dp,
            )
            PetlySheet(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(20.dp),
                ) {
                    PetlyCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.register_card_title),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PetlyTextField(
                            label = stringResource(R.string.label_full_name),
                            value = uiState.nombre,
                            onValueChange = onNombreChange,
                            errorMessage = uiState.nombreError?.let { stringResource(it) },
                            leadingIcon = { PetlyIcon(R.drawable.ic_person) },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next,
                            ),
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        PetlyTextField(
                            label = stringResource(R.string.label_email),
                            value = uiState.correo,
                            onValueChange = onCorreoChange,
                            errorMessage = uiState.correoError?.let { stringResource(it) },
                            leadingIcon = { PetlyIcon(R.drawable.ic_email) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next,
                            ),
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        PetlyPasswordField(
                            label = stringResource(R.string.label_password),
                            value = uiState.contrasena,
                            onValueChange = onContrasenaChange,
                            errorMessage = uiState.contrasenaError?.let { stringResource(it) },
                            imeAction = ImeAction.Next,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        PetlyPasswordField(
                            label = stringResource(R.string.label_confirm_password),
                            value = uiState.confirmacion,
                            onValueChange = onConfirmacionChange,
                            errorMessage = uiState.confirmacionError?.let { stringResource(it) },
                            imeAction = ImeAction.Done,
                            onImeAction = onRegisterClick,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        PetlyPrimaryButton(
                            text = stringResource(R.string.action_register),
                            onClick = onRegisterClick,
                            loading = uiState.isLoading,
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    AuthFooterLink(
                        prompt = stringResource(R.string.register_have_account),
                        action = stringResource(R.string.register_login_link),
                        onClick = onNavigateToLogin,
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

@Preview(showSystemUi = true)
@Composable
private fun RegisterScreenPreview() {
    PetlyTheme {
        RegisterScreen(
            uiState = RegisterUiState(nombre = "Ana López", correoError = R.string.error_invalid_email),
            snackbarHostState = remember { SnackbarHostState() },
            onNombreChange = {},
            onCorreoChange = {},
            onContrasenaChange = {},
            onConfirmacionChange = {},
            onRegisterClick = {},
            onNavigateToLogin = {},
        )
    }
}