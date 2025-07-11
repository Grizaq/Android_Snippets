package com.chirilglance.androidglancedna.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * A specialized input field for OTP (One-Time Password) entry with automatic focus management.
 *
 * @param otpDigits List of strings representing each digit of the OTP
 * @param onDigitChange Callback when a digit changes, providing the index and new value
 * @param onComplete Callback when all digits are filled
 * @param modifier Modifier to be applied to the component
 * @param length Number of digits in the OTP
 * @param shape Shape of each digit field
 * @param spacing Spacing between digit fields
 * @param elevation Shadow elevation of each digit field
 * @param activeColor Border color when a digit field is focused
 * @param inactiveColor Background color for digit fields
 * @param textStyle Text style for the digits
 * @param autoFocusFirst Whether to automatically focus the first field when displayed
 */
@Composable
fun ClubConnectOtpField(
    otpDigits: List<String>,
    onDigitChange: (Int, String) -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    length: Int = otpDigits.size,
    shape: Shape = RoundedCornerShape(16.dp),
    spacing: Dp = 8.dp,
    elevation: Dp = 4.dp,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.surface,
    textStyle: TextStyle = MaterialTheme.typography.headlineMedium.copy(
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    ),
    autoFocusFirst: Boolean = true
) {
    // Ensure otpDigits is the right length
    require(otpDigits.size == length) { "otpDigits list size must match the specified length parameter" }

    val focusRequesters = remember { List(length) { FocusRequester() } }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Track which field has focus
    val focusStates = remember { List(length) { mutableStateOf(false) } }

    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        otpDigits.forEachIndexed { index, digit ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    // Add shadow
                    .shadow(
                        elevation = elevation,
                        shape = shape
                    )
                    .background(
                        color = inactiveColor,
                        shape = shape
                    )
                    // Apply border only to focused item
                    .then(
                        if (focusStates[index].value) {
                            Modifier.border(
                                width = 1.dp,
                                color = activeColor,
                                shape = shape
                            )
                        } else {
                            Modifier
                        }
                    )
            ) {
                BasicTextField(
                    value = digit,
                    onValueChange = { newValue ->
                        if (newValue.length <= 1) {
                            onDigitChange(index, newValue)

                            // Auto-advance focus
                            if (newValue.isNotEmpty() && index < length - 1) {
                                focusRequesters[index + 1].requestFocus()
                            } else if (newValue.isEmpty() && index > 0) {
                                focusRequesters[index - 1].requestFocus()
                            } else if (newValue.isNotEmpty() && index == length - 1) {
                                // Hide keyboard when last digit is entered
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                // Call onComplete when all digits are filled
                                if (otpDigits.all { it.isNotEmpty() }) {
                                    onComplete()
                                }
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = if (index == length - 1) ImeAction.Done else ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (otpDigits.all { it.isNotEmpty() }) {
                            onComplete()
                        }
                    }),
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(focusRequesters[index])
                        .onFocusChanged { focusState ->
                            focusStates[index].value = focusState.isFocused
                        },
                    textStyle = textStyle,
                    singleLine = true,
                    cursorBrush = SolidColor(activeColor),
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            innerTextField()
                        }
                    }
                )
            }
        }
    }

    // Request focus on the first field when the screen loads (if autoFocusFirst is true)
    if (autoFocusFirst) {
        LaunchedEffect(Unit) {
            delay(100) // Small delay to ensure composition is complete
            if (otpDigits.first().isEmpty()) {
                focusRequesters.first().requestFocus()
            } else {
                // Find the first empty field if any
                val firstEmptyIndex = otpDigits.indexOfFirst { it.isEmpty() }
                if (firstEmptyIndex >= 0) {
                    focusRequesters[firstEmptyIndex].requestFocus()
                } else {
                    // All fields are filled, focus the last one
                    focusRequesters.last().requestFocus()
                }
            }
        }
    }
}