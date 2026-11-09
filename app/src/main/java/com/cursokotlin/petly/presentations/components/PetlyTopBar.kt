package com.cursokotlin.petly.presentations.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cursokotlin.petly.R

/** Barra superior sobre el fondo verde: volver, título centrado y una acción opcional a la derecha. */
@Composable
fun PetlyTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes actionIcon: Int? = null,
    actionDescription: String? = null,
    onAction: () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TopBarButton(R.drawable.ic_back, stringResource(R.string.action_back), onBack)
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        if (actionIcon != null) {
            TopBarButton(actionIcon, actionDescription, onAction)
        } else {
            Spacer(modifier = Modifier.size(48.dp)) // mantiene el título centrado
        }
    }
}

@Composable
private fun TopBarButton(
    @DrawableRes icono: Int,
    descripcion: String?,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.15f)),
    ) {
        PetlyIcon(id = icono, contentDescription = descripcion, tint = Color.White)
    }
}