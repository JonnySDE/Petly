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
fun avatarFor(especie: String): Int = when (normalizar(especie)) {
    "perro", "perros" -> R.drawable.ic_avatar_perro
    "gato", "gatos" -> R.drawable.ic_avatar_gato
    "ave", "aves" -> R.drawable.ic_avatar_ave
    "pez", "peces" -> R.drawable.ic_avatar_pez
    "conejo", "conejos" -> R.drawable.ic_avatar_conejo
    "hamster", "hamsters" -> R.drawable.ic_avatar_hamster
    "tortuga", "tortugas" -> R.drawable.ic_avatar_tortuga
    "iguana", "iguanas" -> R.drawable.ic_avatar_iguana
    "serpiente", "serpientes" -> R.drawable.ic_avatar_serpiente
    "tarantula", "tarantulas" -> R.drawable.ic_avatar_tarantula
    else -> R.drawable.ic_avatar_mascota
}

private fun normalizar(texto: String): String =
    java.text.Normalizer.normalize(texto.trim().lowercase(), java.text.Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")