package com.chirilglance.androidglancedna.presentation.examples.auth.phoneverificationscreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectCardDefaults
import com.chirilglance.androidglancedna.core.ui.components.DefaultCard
import com.chirilglance.androidglancedna.presentation.components.form.ValidatedPhoneField
import com.chirilglance.androidglancedna.presentation.examples.auth.google.GoogleSignInButton
import com.chirilglance.androidglancedna.presentation.examples.auth.google.GoogleSignInState
import com.chirilglance.androidglancedna.presentation.examples.auth.google.GoogleSignInViewModel
import com.chirilglance.androidglancedna.presentation.navigation.Screen
import com.chirilglance.androidglancedna.presentation.ui.theme.AccentGreen
import com.chirilglance.androidglancedna.presentation.ui.theme.Navy

@Composable
fun PhoneVerificationScreen(
    navController: NavHostController,
    onVerificationRequested: (String, String) -> Unit, // phoneNumber, verificationId
    onGoogleSignInSuccess: () -> Unit,
    phoneViewModel: PhoneVerificationViewModel = hiltViewModel(),
    googleViewModel: GoogleSignInViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val phoneUiState by phoneViewModel.uiState.collectAsState()
    val googleSignInState by googleViewModel.signInState.collectAsState()

    // Force recomposition when validation state changes
    val canVerify = remember(phoneViewModel.phoneNumber, phoneViewModel.selectedCountry, phoneUiState) {
        phoneViewModel.canVerify() && phoneUiState !is UiState.Loading
    }

    // Activity result launcher for Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        googleViewModel.handleSignInResult(result.data) { user ->
            onGoogleSignInSuccess()
        }
    }

    // Handle phone verification UI state changes
    LaunchedEffect(phoneUiState) {
        when (phoneUiState) {
            is UiState.Success -> {
                val formattedNumber = (phoneUiState as UiState.Success<String>).data
                val verificationId = phoneViewModel.verificationId.value ?: ""

                if (verificationId.isNotEmpty()) {
                    onVerificationRequested(formattedNumber, verificationId)
                }
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
                            phoneNumber = phoneViewModel.phoneNumber,
                            onPhoneNumberChange = { newValue ->
                                phoneViewModel.updatePhoneNumber(newValue)

                                if (phoneViewModel.isPhoneNumberComplete()) {
                                    keyboardController?.hide()
                                }
                            },
                            selectedCountry = phoneViewModel.selectedCountry,
                            onCountrySelected = phoneViewModel::updateSelectedCountry,
                            phoneNumberValidator = phoneViewModel.phoneNumberValidator,
                            externalValidationTriggered = phoneViewModel.formValidator.isValidated.value,
                            onDone = {
                                if (phoneViewModel.canVerify()) {
                                    phoneViewModel.sendVerificationCode(
                                        activity = context as android.app.Activity
                                    ) { phoneNumber, verificationId ->
                                        onVerificationRequested(phoneNumber, verificationId)
                                    }
                                }
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        )

                        // Phone API error message
                        if (phoneUiState is UiState.Error) {
                            Text(
                                text = (phoneUiState as UiState.Error).message,
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

            // Phone verification button
            Button(
                onClick = {
                    keyboardController?.hide()
                    phoneViewModel.sendVerificationCode(
                        activity = context as android.app.Activity
                    ) { phoneNumber, verificationId ->
                        onVerificationRequested(phoneNumber, verificationId)
                    }
                },
                enabled = canVerify,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                if (phoneUiState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Get my code")
                }
            }

            // Divider with "OR"
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = "OR",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Google Sign-In button
            GoogleSignInButton(
                onClick = {
                    val signInIntent = googleViewModel.getSignInIntent()
                    googleSignInLauncher.launch(signInIntent)
                },
                modifier = Modifier.padding(horizontal = 16.dp),
                isLoading = googleSignInState is GoogleSignInState.Loading,
                enabled = googleSignInState !is GoogleSignInState.Loading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email Sign-In link
            TextButton(
                onClick = { navController.navigate(Screen.EmailSignIn.route) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Or sign in with Email",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AccentGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}