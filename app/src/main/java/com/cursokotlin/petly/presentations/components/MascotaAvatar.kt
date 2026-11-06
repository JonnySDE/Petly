package com.cursokotlin.petly.presentations.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cursokotlin.petly.R

@Composable
fun MascotaAvatar(
    especie: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
) {
    Image(
        painter = painterResource(avatarFor(especie)),
        contentDescription = null,
        modifier = modifier
            .size(size)
            .clip(CircleShape),
    )
}

@DrawableRes
fun avatarFor(especie: String): Int = when (especie.trim().lowercase()) {
    "perro" -> R.drawable.ic_avatar_perro
    "gato" -> R.drawable.ic_avatar_gato
    "ave" -> R.drawable.ic_avatar_ave
    else -> R.drawable.ic_avatar_mascota
}