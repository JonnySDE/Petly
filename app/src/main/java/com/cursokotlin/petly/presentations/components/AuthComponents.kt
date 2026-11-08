package com.cursokotlin.petly.presentations.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cursokotlin.petly.R
import com.cursokotlin.petly.ui.theme.PetlyOrange
import com.cursokotlin.petly.ui.theme.PetlyTextSecondary

/** Logo, título y subtítulo sobre el fondo verde (Login y Registro). */
@Composable
fun AuthHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    logoSize: Dp = 88.dp,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier.size(logoSize),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun AuthFooterLink(
    prompt: String,
    action: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = prompt,
            style = MaterialTheme.typography.bodySmall,
            color = PetlyTextSecondary,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = action,
            style = MaterialTheme.typography.labelMedium,
            color = PetlyOrange,
            modifier = Modifier
                .clickable(role = Role.Button, onClick = onClick)
                .padding(vertical = 8.dp),
        )
    }
}