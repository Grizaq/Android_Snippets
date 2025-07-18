package com.chirilglance.androidglancedna.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A specialized text field for phone number input with visual consistency and
 * keyboard handling tailored for phone verification flows.
 *
 * @param value Current phone number value
 * @param onValueChange Callback when phone number changes
 * @param isError Whether the field should display an error state
 * @param onDone Callback when the keyboard's done action is triggered
 * @param modifier Modifier to be applied to the component
 * @param placeholder Placeholder text to display when empty
 * @param elevation Shadow elevation of the card
 * @param shape Shape of the card and text field
 * @param backgroundColor Background color of the card
 * @param allowCharacters Function to determine which characters are allowed (digits, formatting chars, etc.)
 */
@Composable
fun DefaultPhoneField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    onDone: () -> Unit = {},
    modifier: Modifier = Modifier,
    placeholder: String = "Enter Mobile Number",
    elevation: Dp = 4.dp,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    allowCharacters: (Char) -> Boolean = { it.isDigit() || it.isWhitespace() || it in "+-()." }
) {
    val focusManager = LocalFocusManager.current

    Card(
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = modifier
    ) {
        TextField(
            value = value,
            onValueChange = { newValue ->
                // Only allow specified characters
                if (newValue.isEmpty() || newValue.all { allowCharacters(it) }) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                onDone()
            }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                disabledContainerColor = backgroundColor,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                errorIndicatorColor = MaterialTheme.colorScheme.error
            ),
            singleLine = true,
            isError = isError
        )
    }
}