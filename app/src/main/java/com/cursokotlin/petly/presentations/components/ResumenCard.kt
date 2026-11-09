package com.cursokotlin.petly.presentations.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cursokotlin.petly.ui.theme.PetlyTextSecondary

@Composable
fun ResumenCard(
    valor: String,
    etiqueta: String,
    @DrawableRes icono: Int,
    colorValor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                color = colorValor,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    color = PetlyTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                PetlyIcon(id = icono, size = 24.dp)
            }
        }
    }
}