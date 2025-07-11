package com.chirilglance.androidglancedna.presentation.examples.auth.phoneverificationscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectCard
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectCardDefaults
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectCountryCodeSelector
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectPhoneField

@Composable
fun PhoneVerificationScreen(
    onVerificationRequested: (String) -> Unit,
    viewModel: PhoneVerificationViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    val uiState by viewModel.uiState.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val phoneNumberError by viewModel.phoneNumberError.collectAsState()

    // Force recomposition when validation state changes
    val canVerify = remember(phoneNumber, phoneNumberError, uiState) {
        viewModel.canVerify() && uiState !is UiState.Loading
    }

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                val formattedNumber = (uiState as UiState.Success<String>).data
                onVerificationRequested(formattedNumber)
            }
            else -> { /* No action needed */ }
        }
    }

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
                    titleContent = ClubConnectCardDefaults.Title("Let's get you verified"),
                    subtitleContent = ClubConnectCardDefaults.Subtitle("Welcome to Android Glance DNA"),
                    descriptionContent = ClubConnectCardDefaults.Description(
                        "We just need your mobile number to get started!"
                    ),
                    actions = {
                        Column {
                            Column {
                                // Phone input row
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    // Country code selector using the separate component
                                    ClubConnectCountryCodeSelector(
                                        selectedCountry = selectedCountry,
                                        onCountrySelected = viewModel::updateSelectedCountry,
                                        modifier = Modifier.weight(0.35f)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Phone number input using the separate component
                                    ClubConnectPhoneField(
                                        value = phoneNumber,
                                        onValueChange = { newValue ->
                                            viewModel.updatePhoneNumber(newValue)

                                            // Check if we need to hide the keyboard after each digit is entered
                                            if (viewModel.isPhoneNumberComplete()) {
                                                keyboardController?.hide()
                                            }
                                        },
                                        isError = phoneNumberError != null,
                                        onDone = {
                                            if (viewModel.canVerify()) {
                                                viewModel.verifyPhoneNumber { formattedNumber ->
                                                    onVerificationRequested(formattedNumber)
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(0.7f)
                                    )
                                }

                                // Error message centered below both inputs
                                phoneNumberError?.let { error ->
                                    Text(
                                        text = error,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp)
                                    )
                                }
                            }

                            // API error message
                            if (uiState is UiState.Error) {
                                Text(
                                    text = (uiState as UiState.Error).message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                )
                            }
                        }
                    }
                )

                // Extra space at the bottom to ensure button doesn't overlap
                Spacer(modifier = Modifier.height(100.dp))
            }

            // Bottom button with proper padding
            Button(
                onClick = {
                    keyboardController?.hide()
                    viewModel.verifyPhoneNumber { formattedNumber ->
                        onVerificationRequested(formattedNumber)
                    }
                },
                enabled = canVerify,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .imePadding()
            ) {
                if (uiState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Get my code")
                }
            }
        }
    }
}