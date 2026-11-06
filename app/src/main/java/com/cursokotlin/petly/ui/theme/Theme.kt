package com.cursokotlin.petly.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val PetlyColorScheme = lightColorScheme(
    primary = PetlyTealDark,
    onPrimary = Color.White,
    secondary = PetlyOrange,
    onSecondary = Color.White,
    background = PetlyScreenBackground,
    onBackground = PetlyTextPrimary,
    surface = Color.White,
    onSurface = PetlyTextPrimary,
    onSurfaceVariant = PetlyTextSecondary,
    outline = PetlyBorder,
    error = PetlyError,
    onError = Color.White,
)

@Composable
fun PetlyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PetlyColorScheme,
        content = content,
    )
}