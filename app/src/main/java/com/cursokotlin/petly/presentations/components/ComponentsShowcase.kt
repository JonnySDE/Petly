package com.cursokotlin.petly.presentations.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cursokotlin.petly.R
import com.cursokotlin.petly.ui.theme.PetlyTheme

@Composable
fun ComponentsShowcase() {
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

    PetlyBackground {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 32.dp),
            ) {
                Text(
                    text = "Petly",
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall,
                )
                Text(
                    text = "Regístrate y lleva el control de tus mascotas",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            PetlySheet(modifier = Modifier.weight(1f).fillMaxWidth()) {
                PetlyCard(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Iniciar Sesión", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(16.dp))
                    PetlyTextField(
                        label = "Correo electrónico",
                        value = correo,
                        onValueChange = { correo = it },
                        leadingIcon = { Icon(painterResource(R.drawable.ic_email), null, modifier = Modifier.size(24.dp)) },
                    )
                    Spacer(Modifier.height(12.dp))
                    PetlyTextField(
                        label = "Contraseña",
                        value = clave,
                        onValueChange = { clave = it },
                        errorMessage = if (clave.isNotEmpty() && clave.length < 8) "Mínimo 8 caracteres" else null,
                        leadingIcon = { Icon(painterResource(R.drawable.ic_password), null, modifier = Modifier.size(24.dp)) },
                        visualTransformation = PasswordVisualTransformation(),
                    )
                    Spacer(Modifier.height(20.dp))
                    PetlyPrimaryButton(text = "Ingresar a Petly", onClick = {})
                    Spacer(Modifier.height(10.dp))
                    PetlyOutlinedButton(text = "Cancelar", onClick = {})
                    Spacer(Modifier.height(20.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MascotaAvatar(especie = "Perro")
                        MascotaAvatar(especie = "Gato")
                        MascotaAvatar(especie = "Ave")
                        MascotaAvatar(especie = "Otro")
                    }
                }
            }
        }
    }
}
