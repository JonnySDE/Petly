package com.cursokotlin.petly.presentations.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.cursokotlin.petly.ui.theme.PetlyTeal
import com.cursokotlin.petly.ui.theme.PetlyTealDark

private val ButtonShape = RoundedCornerShape(14.dp)
private val DisabledBrush = Brush.horizontalGradient(listOf(Color(0xFF9DB7B2), Color(0xFF9DB7B2)))

/** Botón principal */
@Composable
fun PetlyPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    val activo = enabled && !loading
    val brush = if (activo || loading) {
        Brush.horizontalGradient(listOf(PetlyTeal, PetlyTealDark))
    } else {
        DisabledBrush
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(ButtonShape)
            .background(brush)
            .clickable(enabled = activo, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color.White,
                strokeWidth = 2.dp,
            )
        } else {
            Text(text = text, color = Color.White, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** Botón secundario con borde (Cancelar, Editar datos...). */
@Composable
fun PetlyOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled,
        shape = ButtonShape,
        border = BorderStroke(1.5.dp, PetlyTealDark),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = PetlyTealDark),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}