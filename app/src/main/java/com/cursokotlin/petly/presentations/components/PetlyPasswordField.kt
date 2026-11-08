package com.cursokotlin.petly.presentations.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.cursokotlin.petly.R
import androidx.compose.ui.unit.dp

@Composable
fun PetlyPasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
) {
    var visible by rememberSaveable { mutableStateOf(false) }

    PetlyTextField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        errorMessage = errorMessage,
        leadingIcon = { PetlyIcon(R.drawable.ic_password) },
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                PetlyIcon(
                    id = if (visible) R.drawable.ic_visibility_off else R.drawable.ic_visibility,
                    contentDescription = stringResource(
                        if (visible) R.string.hide_password else R.string.show_password,
                    ),
                    size = 32.dp,
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),

        keyboardActions = if (imeAction == ImeAction.Done) {
            KeyboardActions(onDone = { onImeAction() })
        } else {
            KeyboardActions.Default
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
    )
}