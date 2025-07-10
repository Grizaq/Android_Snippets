package com.chirilglance.androidglancedna.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chirilglance.androidglancedna.presentation.ui.theme.ErrorRed
import com.chirilglance.androidglancedna.presentation.ui.theme.HintColor
import com.chirilglance.androidglancedna.presentation.ui.theme.Navy

/**
 * A reusable text field component with standard styling and validation.
 */
@Composable
fun ClubConnectTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    hint: String = "",
    maxLength: Int = Int.MAX_VALUE,
    errorMessage: String? = null,
    isActive: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: @Composable (() -> Unit)? = null,
    textColor: Color = Navy,
    allowSpaces: Boolean = true,
    readOnly: Boolean = false
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Navy,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Text field
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // Apply constraints (max length, spaces)
                val filtered = if (!allowSpaces) {
                    newValue.replace(" ", "")
                } else {
                    newValue
                }

                if (filtered.length <= maxLength) {
                    onValueChange(filtered)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = hint, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)) },
            isError = errorMessage != null,
            readOnly = readOnly,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = trailingIcon,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outline,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                errorTextColor = textColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                errorBorderColor = ErrorRed,
                errorLabelColor = ErrorRed
            )
        )

        // Error message
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

/**
 * A text field specifically for number input
 */
@Composable
fun NumberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    hint: String = "",
    errorMessage: String? = null,
    isInteger: Boolean = true,
    minValue: Float? = null,
    maxValue: Float? = null,
    readOnly: Boolean = false
) {
    ClubConnectTextField(
        value = value,
        onValueChange = { newValue ->
            // Only allow numeric input
            val isValid = if (isInteger) {
                newValue.all { it.isDigit() }
            } else {
                newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))
            }

            if (isValid) {
                // Check min/max if set
                val numValue = newValue.toFloatOrNull()
                val withinRange = numValue == null ||
                        (minValue == null || numValue >= minValue) &&
                        (maxValue == null || numValue <= maxValue)

                if (withinRange) {
                    onValueChange(newValue)
                }
            }
        },
        label = label,
        hint = hint,
        errorMessage = errorMessage,
        keyboardType = if (isInteger) KeyboardType.Number else KeyboardType.Decimal,
        allowSpaces = false,
        readOnly = readOnly,
        modifier = modifier
    )
}

/**
 * A label and text field in a row layout, wrapped in a card
 */
@Composable
fun LabeledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    hint: String = "",
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    labelWeight: Float = 0.4f,
    fieldWeight: Float = 0.6f,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
) {
    Column(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(labelWeight)
                        .padding(end = 8.dp)
                )

                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(fieldWeight),
                    placeholder = {
                        Text(
                            text = hint,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    isError = errorMessage != null,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = ErrorRed,
                        focusedPlaceholderColor = HintColor,
                        unfocusedPlaceholderColor = HintColor
                    ),
                    singleLine = true
                )
            }
        }

        // Error message
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}