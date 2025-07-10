package com.chirilglance.androidglancedna.presentation.examples.auth.otpscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectCard
import com.chirilglance.androidglancedna.core.ui.components.PrimaryButton
import kotlinx.coroutines.delay

@Composable
fun OtpVerificationScreen(
    phoneNumber: String,
    onVerificationComplete: () -> Unit,
    onBackPressed: () -> Unit,
    viewModel: OtpVerificationViewModel = hiltViewModel()
) {
    // Initialize the viewModel with the phone number
    LaunchedEffect(phoneNumber) {
        viewModel.initPhoneNumber(phoneNumber)
    }

    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val otpDigits by viewModel.otpDigits.collectAsState()
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()

    // Use Scaffold for better inset handling
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Scrollable content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                ClubConnectCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Verify your account",
                    subtitle = "SMS Verification",
                    description = "Enter the code we have sent to your mobile device and enter it below to link your account.",
                    actions = {
                        Column {
                            // OTP input field
                            OtpField(
                                otpDigits = otpDigits,
                                onDigitChange = viewModel::updateOtpDigit,
                                onComplete = {
                                    // Auto-verify when all digits are entered
                                    if (otpDigits.all { it.isNotEmpty() }) {
                                        viewModel.verifyOtp {
                                            onVerificationComplete()
                                        }
                                    }
                                }
                            )

                            // Display error message if any
                            if (uiState is UiState.Error) {
                                Text(
                                    text = (uiState as UiState.Error).message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }

                            // Resend timer
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (remainingSeconds > 0) {
                                    Text(
                                        text = "Resend code in ${remainingSeconds}s",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    TextButton(
                                        onClick = { viewModel.resendOtp() },
                                        enabled = uiState !is UiState.Loading
                                    ) {
                                        Text("Resend code")
                                    }
                                }
                            }
                        }
                    }
                )

                // Extra space at the bottom to ensure buttons don't overlap
                Spacer(modifier = Modifier.height(100.dp))
            }

            // Bottom buttons with proper padding
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .imePadding() // This is the key - ensures buttons stay above keyboard
            ) {
                // In OtpVerificationScreen.kt
                PrimaryButton(
                    text = "Verify",
                    onClick = {
                        viewModel.verifyOtp {
                            onVerificationComplete()
                        }
                    },
                    enabled = otpDigits.all { it.isNotEmpty() },
                    isLoading = uiState is UiState.Loading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onBackPressed,
                    enabled = uiState !is UiState.Loading,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Back to Phone Number")
                }
            }
        }
    }
}

@Composable
fun OtpField(
    otpDigits: List<String>,
    onDigitChange: (Int, String) -> Unit,
    onComplete: () -> Unit
) {
    val otpLength = 4
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Track which field has focus
    val focusStates = remember { List(otpLength) { mutableStateOf(false) } }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
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
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    )
                    // Apply border only to focused item
                    .then(
                        if (focusStates[index].value) {
                            Modifier.border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp)
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
                            if (newValue.isNotEmpty() && index < otpLength - 1) {
                                focusRequesters[index + 1].requestFocus()
                            } else if (newValue.isEmpty() && index > 0) {
                                focusRequesters[index - 1].requestFocus()
                            } else if (newValue.isNotEmpty() && index == otpLength - 1) {
                                // Hide keyboard when last digit is entered
                                keyboardController?.hide()
                                // Call onComplete when all digits are filled
                                onComplete()
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
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
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
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

    LaunchedEffect(Unit) {
        delay(100) // Small delay to ensure composition is complete
        focusRequesters.first().requestFocus()
    }

    // Request focus on the first field when the screen loads
    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
    }
}