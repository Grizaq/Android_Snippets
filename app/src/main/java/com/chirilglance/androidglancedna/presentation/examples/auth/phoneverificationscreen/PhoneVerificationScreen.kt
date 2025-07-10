package com.chirilglance.androidglancedna.presentation.examples.auth.phoneverificationscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectCard
import com.chirilglance.androidglancedna.domain.models.CountryCode
import com.chirilglance.androidglancedna.domain.utils.CountryCodeProvider

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
                    title = "Let's get you verified",
                    subtitle = "Welcome to Android Glance DNA",
                    description = "We just need your mobile number to get started!",
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
                                    // Country code selector
                                    CountryCodeSelector(
                                        selectedCountry = selectedCountry,
                                        onCountrySelected = viewModel::updateSelectedCountry,
                                        modifier = Modifier.weight(0.35f)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Phone number input
                                    PhoneNumberField(
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
                                            keyboardController?.hide()
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

@Composable
fun CountryCodeSelector(
    selectedCountry: CountryCode,
    onCountrySelected: (CountryCode) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = modifier) {
        // Selected country display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable {
                    keyboardController?.hide()
                    expanded = true
                },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()  // Fill the entire Card
                    .padding(horizontal = 12.dp),  // Only horizontal padding
                verticalAlignment = Alignment.CenterVertically,  // This will center content vertically
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Country flag emoji and code
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Flag emoji
                    Text(
                        text = CountryCodeProvider.codeToEmoji(selectedCountry.code),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Dial code
                    Text(
                        text = selectedCountry.dialCode,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Dropdown arrow
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Country"
                )
            }
        }

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(240.dp)
                .heightIn(max = 350.dp)
        ) {
            CountryCodeProvider.getAllCountryCodes().forEach { country ->
                DropdownMenuItem(text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = CountryCodeProvider.codeToEmoji(country.code),
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${country.name} (${country.dialCode})",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }, onClick = {
                    onCountrySelected(country)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun PhoneNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        TextField(
            value = value,
            onValueChange = { newValue ->
                // Only allow digits, spaces, and some formatting characters
                if (newValue.isEmpty() || newValue.all { it.isDigit() || it.isWhitespace() || it in "+-()." }) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Enter Mobile Number",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
                onDone()
            }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true,
            isError = isError
        )
    }
}