package com.chirilglance.androidglancedna.presentation.examples.auth.phoneverificationscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectCardDefaults
import com.chirilglance.androidglancedna.core.ui.components.DefaultCard
import com.chirilglance.androidglancedna.presentation.components.form.ValidatedPhoneField

@Composable
fun PhoneVerificationScreen(
    onVerificationRequested: (String) -> Unit,
    viewModel: PhoneVerificationViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    val uiState by viewModel.uiState.collectAsState()

    // Force recomposition when validation state changes
    val canVerify = remember(viewModel.phoneNumber, viewModel.selectedCountry, uiState) {
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
                titleContent = ClubConnectCardDefaults.Title("Let's get you verified"),
                subtitleContent = ClubConnectCardDefaults.Subtitle("Welcome to Android Glance DNA"),
                descriptionContent = ClubConnectCardDefaults.Description(
                    "We just need your mobile number to get started!"
                ),
                actions = {
                    Column {
                        // Validated phone field with country code selector
                        ValidatedPhoneField(
                            phoneNumber = viewModel.phoneNumber,
                            onPhoneNumberChange = { newValue ->
                                viewModel.updatePhoneNumber(newValue)

                                // Check if we need to hide the keyboard after each digit
                                if (viewModel.isPhoneNumberComplete()) {
                                    keyboardController?.hide()
                                }
                            },
                            selectedCountry = viewModel.selectedCountry,
                            onCountrySelected = viewModel::updateSelectedCountry,
                            phoneNumberValidator = viewModel.phoneNumberValidator,
                            externalValidationTriggered = viewModel.formValidator.isValidated.value,
                            onDone = {
                                if (viewModel.canVerify()) {
                                    viewModel.verifyPhoneNumber { formattedNumber ->
                                        onVerificationRequested(formattedNumber)
                                    }
                                }
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        )

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

            Spacer(modifier = Modifier.height(16.dp))

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
                    .padding(16.dp)
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