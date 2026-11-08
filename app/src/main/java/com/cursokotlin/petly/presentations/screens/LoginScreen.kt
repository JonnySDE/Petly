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
import com.cursokotlin.petly.presentations.viewmodels.LoginUiState
import com.cursokotlin.petly.presentations.viewmodels.LoginViewModel
import com.cursokotlin.petly.ui.theme.PetlyTheme

@Composable
fun LoginRoute(
    viewModel: LoginViewModel,
    registroExitoso: Boolean,
    onRegistroExitosoMostrado: () -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
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

    val textoRegistro = stringResource(R.string.register_success)
    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            snackbarHostState.showSnackbar(textoRegistro)
            onRegistroExitosoMostrado()
        }
    }

    LaunchedEffect(uiState.sesionIniciada) {
        if (uiState.sesionIniciada) {
            onLoginSuccess()
            viewModel.onNavegacionConsumida()
        }
    }

    LoginScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onCorreoChange = viewModel::onCorreoChange,
        onContrasenaChange = viewModel::onContrasenaChange,
        onLoginClick = viewModel::onLoginClick,
        onNavigateToRegister = onNavigateToRegister,
    )
}

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    snackbarHostState: SnackbarHostState,
    onCorreoChange: (String) -> Unit,
    onContrasenaChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    PetlyBackground {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            AuthHeader(
                title = stringResource(R.string.app_name),
                subtitle = stringResource(R.string.login_subtitle),
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
                            text = stringResource(R.string.login_title),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
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
                            imeAction = ImeAction.Done,
                            onImeAction = onLoginClick,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        PetlyPrimaryButton(
                            text = stringResource(R.string.action_login),
                            onClick = onLoginClick,
                            loading = uiState.isLoading,
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    AuthFooterLink(
                        prompt = stringResource(R.string.login_no_account),
                        action = stringResource(R.string.login_register_link),
                        onClick = onNavigateToRegister,
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
private fun LoginScreenPreview() {
    PetlyTheme {
        LoginScreen(
            uiState = LoginUiState(correo = "ana@correo.com", contrasenaError = R.string.error_required),
            snackbarHostState = remember { SnackbarHostState() },
            onCorreoChange = {},
            onContrasenaChange = {},
            onLoginClick = {},
            onNavigateToRegister = {},
        )
    }
}