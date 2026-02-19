package com.chirilglance.androidglancedna.presentation.examples.auth.otpscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectCardDefaults
import com.chirilglance.androidglancedna.core.ui.components.DefaultCard
import com.chirilglance.androidglancedna.core.ui.components.DefaultOtpField
import com.chirilglance.androidglancedna.core.ui.components.buttons.PrimaryButton
import com.google.firebase.auth.PhoneAuthProvider

@Composable
fun OtpVerificationScreen(
    phoneNumber: String,
    verificationId: String,
    resendToken: PhoneAuthProvider.ForceResendingToken? = null,
    onVerificationComplete: () -> Unit,
    onBackPressed: () -> Unit,
    viewModel: OtpVerificationViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Initialize the viewModel with verification data
    LaunchedEffect(phoneNumber, verificationId) {
        viewModel.initVerification(phoneNumber, verificationId, resendToken)
    }

    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val otpDigits by viewModel.otpDigits.collectAsState()
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            DefaultCard(
                modifier = Modifier.fillMaxWidth(),
                titleContent = ClubConnectCardDefaults.Title("Verify your account"),
                subtitleContent = ClubConnectCardDefaults.Subtitle("SMS Verification"),
                descriptionContent = ClubConnectCardDefaults.Description(
                    "Enter the 6-digit code we sent to your mobile device"
                ),
                actions = {
                    Column {
                        // OTP input field - 6 digits for Firebase
                        DefaultOtpField(
                            otpDigits = otpDigits,
                            onDigitChange = viewModel::updateOtpDigit,
                            onComplete = {
                                // Auto-verify when all 6 digits are entered
                                if (otpDigits.all { it.isNotEmpty() }) {
                                    viewModel.verifyOtp {
                                        onVerificationComplete()
                                    }
                                }
                            },
                            modifier = Modifier.padding(vertical = 16.dp)
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
                                    onClick = {
                                        viewModel.resendOtp(context as android.app.Activity)
                                    },
                                    enabled = uiState !is UiState.Loading
                                ) {
                                    Text("Resend code")
                                }
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom buttons with proper padding
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
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