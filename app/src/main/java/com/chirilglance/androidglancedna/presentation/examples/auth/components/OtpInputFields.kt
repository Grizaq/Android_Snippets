package com.chirilglance.androidglancedna.presentation.examples.auth.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A component for OTP code input with multiple digit fields
 *
 * @param otpDigits List of OTP digit values
 * @param onOtpChanged Callback when OTP digits change
 * @param onComplete Callback when all OTP digits are filled
 * @param modifier Modifier for the component
 * @param digitCount Number of digits in the OTP
 */
@Composable
fun OtpInputFields(
    otpDigits: List<String>,
    onOtpChanged: (index: Int, digit: String) -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    digitCount: Int = 6
) {
    val focusRequesters = remember { List(digitCount) { FocusRequester() } }
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        otpDigits.forEachIndexed { index, digit ->
            OtpDigitInput(
                value = digit,
                onValueChange = { newValue ->
                    if (newValue.length <= 1) {
                        onOtpChanged(index, newValue)

                        // Auto-advance focus
                        if (newValue.isNotEmpty() && index < digitCount - 1) {
                            focusRequesters[index + 1].requestFocus()
                        } else if (newValue.isEmpty() && index > 0) {
                            focusRequesters[index - 1].requestFocus()
                        } else if (index == digitCount - 1 && newValue.isNotEmpty()) {
                            keyboardController?.hide()
                            onComplete()
                        }
                    }
                },
                focusRequester = focusRequesters[index],
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            )
        }
    }

    // Request focus on first field when displayed
    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }
}

/**
 * A single digit input field for OTP
 */
@Composable
fun OtpDigitInput(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(width = 48.dp, height = 56.dp)
            .border(
                width = 1.dp,
                color = if (value.isEmpty())
                    MaterialTheme.colorScheme.outline
                else
                    MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.small
            ),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.Center
                ),
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        innerTextField()
                    }
                }
            )

            // Show placeholder if empty
            if (value.isEmpty()) {
                Text(
                    text = "•",
                    fontSize = 24.sp,
                    color = Color.Gray.copy(alpha = 0.3f)
                )
            }
        }
    }
}