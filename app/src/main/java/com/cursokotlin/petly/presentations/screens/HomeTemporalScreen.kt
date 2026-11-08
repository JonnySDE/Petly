package com.cursokotlin.petly.presentations.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cursokotlin.petly.presentations.components.PetlyBackground
import com.cursokotlin.petly.presentations.components.PetlyPrimaryButton
import com.cursokotlin.petly.presentations.viewmodels.HomeViewModel

@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    onSesionCerrada: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.sesionCerrada) {
        if (uiState.sesionCerrada) {
            onSesionCerrada()
            viewModel.onNavegacionConsumida()
        }
    }

    HomeTemporalScreen(
        nombre = uiState.nombreUsuario,
        onCerrarSesion = viewModel::onCerrarSesionClick,
    )
}

@Composable
fun HomeTemporalScreen(
    nombre: String?,
    onCerrarSesion: () -> Unit,
) {
    PetlyBackground {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "¡Hola de nuevo!", color = Color.White, style = MaterialTheme.typography.titleMedium)
            Text(text = nombre.orEmpty(), color = Color.White, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))
            PetlyPrimaryButton(text = "Cerrar sesión", onClick = onCerrarSesion)
        }
    }
}