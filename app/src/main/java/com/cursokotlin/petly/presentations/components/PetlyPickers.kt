package com.cursokotlin.petly.presentations.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.cursokotlin.petly.R


@Composable
fun PetlyPickerField(
    label: String,
    value: String,
    onClick: () -> Unit,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    errorMessage: String? = null,
) {
    Box(modifier = modifier) {
        PetlyTextField(
            label = label,
            value = value,
            onValueChange = {},
            readOnly = true,
            placeholder = placeholder,
            errorMessage = errorMessage,
            trailingIcon = { PetlyIcon(id = iconRes) },
        )
        Box(modifier = Modifier.matchParentSize().clickable(role = Role.Button, onClick = onClick))
    }
}

/** Lista desplegable. Solo guarda si el menú está abierto; el valor y las opciones vienen de fuera. */
@Composable
fun PetlyDropdownField(
    label: String,
    value: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier) {
        PetlyPickerField(
            label = label,
            value = value,
            onClick = { expanded = true },
            iconRes = R.drawable.ic_dropdown,
            errorMessage = errorMessage,
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onOptionSelected(opcion)
                        expanded = false
                    },
                )
            }
        }
    }
}