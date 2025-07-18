package com.chirilglance.androidglancedna.core.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chirilglance.androidglancedna.presentation.ui.theme.ErrorRed
import com.chirilglance.androidglancedna.presentation.ui.theme.HintColor
import com.chirilglance.androidglancedna.presentation.ui.theme.Navy

/**
 * A reusable text field component with standard styling and validation.
 *
 * @param value Current text value to display
 * @param onValueChange Callback when text changes
 * @param label Text label displayed above the input field
 * @param modifier Modifier to be applied to the component
 * @param hint Placeholder text shown when the field is empty
 * @param maxLength Maximum character length (defaults to unlimited)
 * @param errorMessage Optional error message to display below the field
 * @param isActive Whether the field should appear active/highlighted
 * @param keyboardType Type of keyboard to display (text, number, email, etc.)
 * @param imeAction Action button to show on the keyboard
 * @param onDone Callback when the keyboard's action button is pressed
 * @param trailingIcon Optional composable for trailing icon
 * @param leadingIcon Optional composable for leading icon
 * @param textColor Color for the input text
 * @param allowSpaces Whether spaces should be accepted in the input
 * @param readOnly Whether the field should be editable
 * @param singleLine Whether to restrict input to a single line
 */
@Composable
fun DefaultTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    hint: String = "",
    maxLength: Int = Int.MAX_VALUE,
    errorMessage: String? = null,
    isActive: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    onDone: (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    textColor: Color = Navy,
    allowSpaces: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = false
) {
    val focusManager = LocalFocusManager.current

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
            placeholder = {
                Text(
                    text = hint,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            },
            isError = errorMessage != null,
            readOnly = readOnly,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onDone?.invoke()
                }
            ),
            trailingIcon = trailingIcon,
            leadingIcon = leadingIcon,
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
 * A label and text field in a row layout, wrapped in a card for a compact form element.
 *
 * @param value Current text value to display
 * @param onValueChange Callback when text changes
 * @param label Text label displayed beside the input field
 * @param modifier Modifier to be applied to the component
 * @param hint Placeholder text shown when the field is empty
 * @param errorMessage Optional error message to display below the card
 * @param keyboardType Type of keyboard to display
 * @param imeAction Action button to show on the keyboard
 * @param onDone Callback when the keyboard's action button is pressed
 * @param labelWeight Weight of the label in the row (0-1)
 * @param fieldWeight Weight of the field in the row (0-1)
 * @param containerColor Background color of the card
 * @param elevation Shadow depth of the card
 * @param contentPadding Padding inside the card
 * @param singleLine Whether to restrict input to a single line
 * @param isFocused Whether the field is currently focused
 * @param onFocusChanged Callback when focus state changes
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
    imeAction: ImeAction = ImeAction.Done,
    onDone: (() -> Unit)? = null,
    labelWeight: Float = 0.3f,
    fieldWeight: Float = 0.7f,
    maxLines: Int = 2,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
    singleLine: Boolean = true,
    isFocused: Boolean = false,
    onFocusChanged: ((Boolean) -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isFocused) {
                    Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier
                }
            )
    ) {
        Column {
            // Input field row
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
                    modifier = Modifier
                        .weight(fieldWeight)
                        .onFocusChanged { focusState ->
                            onFocusChanged?.invoke(focusState.isFocused)
                        },
                    placeholder = {
                        Text(
                            text = hint,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = imeAction
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            onDone?.invoke()
                        }
                    ),
                    isError = errorMessage != null,
                    singleLine = singleLine,
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
                    maxLines = maxLines,
                    visualTransformation = visualTransformation
                )
            }

            // Error message with consistent padding
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp, end = 16.dp)
                )
            }
        }
    }
}

/**
 * A text field specifically designed for numeric input with optional validation.
 *
 * @param value Current text value to display
 * @param onValueChange Callback when text changes
 * @param label Text label displayed above the input field
 * @param modifier Modifier to be applied to the component
 * @param hint Placeholder text shown when the field is empty
 * @param errorMessage Optional error message to display below the field
 * @param isInteger Whether to restrict input to whole numbers only
 * @param minValue Optional minimum value constraint
 * @param maxValue Optional maximum value constraint
 * @param imeAction Action button to show on the keyboard
 * @param onDone Callback when the keyboard's action button is pressed
 * @param readOnly Whether the field should be editable
 * @param singleLine Whether to restrict input to a single line
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
    imeAction: ImeAction = ImeAction.Done,
    onDone: (() -> Unit)? = null,
    maxLines: Int = 1,
    singleLine: Boolean = true,
    isFocused: Boolean = false,
    onFocusChanged: ((Boolean) -> Unit)? = null
) {
    LabeledTextField(
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
        imeAction = imeAction,
        onDone = onDone,
        maxLines = maxLines,
        singleLine = singleLine,
        isFocused = isFocused,
        onFocusChanged = onFocusChanged,
        modifier = modifier
    )
}