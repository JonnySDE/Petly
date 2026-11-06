package com.cursokotlin.petly.presentations.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cursokotlin.petly.ui.theme.PetlyBorder
import com.cursokotlin.petly.ui.theme.PetlyGreenCircle
import com.cursokotlin.petly.ui.theme.PetlyGreenDark
import com.cursokotlin.petly.ui.theme.PetlyGreenMid
import com.cursokotlin.petly.ui.theme.PetlyScreenBackground

/** Fondo de cabecera */
@Composable
fun PetlyBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(PetlyGreenDark, PetlyGreenMid)))
            .clipToBounds(),
    ) {
        DecorativeCircle(
            size = 170.dp,
            modifier = Modifier.align(Alignment.TopEnd).offset(x = 50.dp, y = 10.dp),
        )
        DecorativeCircle(
            size = 90.dp,
            modifier = Modifier.align(Alignment.TopStart).offset(x = (-30).dp, y = 90.dp),
        )
        content()
    }
}

@Composable
private fun DecorativeCircle(size: Dp, modifier: Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .background(PetlyGreenCircle.copy(alpha = 0.55f), CircleShape),
    )
}

/** Hoja de contenido con esquinas superiores redondeadas que se apoya sobre el fondo verde. */
@Composable
fun PetlySheet(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        color = PetlyScreenBackground,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
    ) {
        Column(content = content)
    }
}

/** Tarjeta blanca con borde suave (formularios, elementos de lista, fichas). */
@Composable
fun PetlyCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, PetlyBorder),
    ) {
        Column(modifier = Modifier.padding(20.dp), content = content)
    }
}