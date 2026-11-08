package com.cursokotlin.petly.presentations.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.cursokotlin.petly.ui.theme.PetlyBorder
import com.cursokotlin.petly.ui.theme.PetlyError
import com.cursokotlin.petly.ui.theme.PetlyInputBackground
import com.cursokotlin.petly.ui.theme.PetlyTealDark
import com.cursokotlin.petly.ui.theme.PetlyTextPrimary
import androidx.compose.foundation.text.KeyboardActions

@Composable
fun PetlyTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    errorMessage: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = PetlyTextPrimary,
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            keyboardActions = keyboardActions,
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = placeholder?.let { { Text(it) } },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = errorMessage != null,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = PetlyInputBackground,
                unfocusedContainerColor = PetlyInputBackground,
                disabledContainerColor = PetlyInputBackground,
                errorContainerColor = PetlyInputBackground,
                focusedBorderColor = PetlyTealDark,
                unfocusedBorderColor = PetlyBorder,
                errorBorderColor = PetlyError,
            ),
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = PetlyError,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
            )
        }
    }
}