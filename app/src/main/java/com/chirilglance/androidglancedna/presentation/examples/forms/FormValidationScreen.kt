package com.chirilglance.androidglancedna.presentation.examples.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.DateOfBirthField
import com.chirilglance.androidglancedna.core.ui.components.EventDateField
import com.chirilglance.androidglancedna.core.ui.components.LabeledTextField
import com.chirilglance.androidglancedna.core.ui.components.buttons.PrimaryButton
import com.chirilglance.androidglancedna.core.ui.components.buttons.SecondaryButton
import com.chirilglance.androidglancedna.presentation.components.form.ValidatedPhoneField

@Composable
fun FormValidationScreen(
    viewModel: FormValidationViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    val formSubmissionState by viewModel.formSubmissionState.collectAsState()
    val fieldErrors by viewModel.fieldErrors.collectAsState()

    // Monitor form submission state for success/error
    LaunchedEffect(formSubmissionState) {
        when (formSubmissionState) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar("Form submitted successfully!")
            }

            is UiState.Error -> {
                snackbarHostState.showSnackbar((formSubmissionState as UiState.Error).message)
            }

            else -> { /* No action needed */ }
        }
    }

    // Reset all validation flags when screen is first loaded
    LaunchedEffect(key1 = Unit) {
        viewModel.resetValidationFlags()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Form Validation Example",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "This form demonstrates the validation system",
            style = MaterialTheme.typography.bodyLarge
        )

        // Form Fields - Basic Information
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section Title
            Text(
                text = "Basic Information",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Please fill out your details. All fields are required for a complete profile.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Full Name field with validation
            LabeledTextField(
                value = viewModel.fullName,
                onValueChange = viewModel::updateFullName,
                label = "Full Name",
                hint = "Enter your full name",
                errorMessage = fieldErrors["fullName"],
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                singleLine = true,
                maxLines = 1,
                onFocusChanged = { isFocused ->
                    if (!isFocused) {
                        viewModel.markFieldAsTouched("fullName")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Email field with validation
            LabeledTextField(
                value = viewModel.email,
                onValueChange = viewModel::updateEmail,
                label = "Email Address",
                hint = "your@email.com",
                errorMessage = fieldErrors["email"],
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                singleLine = true,
                maxLines = 1,
                onFocusChanged = { isFocused ->
                    if (!isFocused) {
                        viewModel.markFieldAsTouched("email")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Phone field with validation
            ValidatedPhoneField(
                phoneNumber = viewModel.phoneNumber,
                onPhoneNumberChange = viewModel::updatePhoneNumber,
                selectedCountry = viewModel.selectedCountry,
                onCountrySelected = viewModel::updateSelectedCountry,
                phoneNumberValidator = viewModel.phoneNumberValidator,
                externalValidationTriggered = fieldErrors.containsKey("phone") && fieldErrors["phone"] != null,
                onDone = null,
                initiallyValidated = false,
                validateOnChange = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Date of Birth field with validation
            DateOfBirthField(
                selectedDate = viewModel.birthDate,
                onDateSelected = viewModel::updateBirthDate,
                errorMessage = fieldErrors["birthDate"],
                modifier = Modifier.fillMaxWidth()
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Form Fields - Additional Information
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section Title
            Text(
                text = "Additional Information",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Event Date field with time
            EventDateField(
                selectedDate = viewModel.eventDate,
                onDateSelected = viewModel::updateEventDate,
                includeTime = true,
                selectedTime = viewModel.eventTime,
                onTimeSelected = viewModel::updateEventTime,
                errorMessage = fieldErrors["eventDate"],
                label = "Event Date & Time",
                hint = "Select event date and time",
                modifier = Modifier.fillMaxWidth(),
                allowPastDates = false
            )

            // Password field with validation
            LabeledTextField(
                value = viewModel.password,
                onValueChange = viewModel::updatePassword,
                label = "Password",
                hint = "Enter a secure password",
                errorMessage = fieldErrors["password"],
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
                singleLine = true,
                maxLines = 1,
                contentPadding = PaddingValues(
                    horizontal = 16.dp, vertical = 8.dp
                ),
                visualTransformation = PasswordVisualTransformation(),
                onFocusChanged = { isFocused ->
                    if (!isFocused) {
                        viewModel.markFieldAsTouched("password")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Password confirmation field
            LabeledTextField(
                value = viewModel.passwordConfirmation,
                onValueChange = viewModel::updatePasswordConfirmation,
                label = "Confirm Password",
                hint = "Re-enter your password",
                errorMessage = fieldErrors["passwordConfirmation"],
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
                singleLine = true,
                maxLines = 1,
                contentPadding = PaddingValues(
                    horizontal = 16.dp, vertical = 8.dp
                ),
                visualTransformation = PasswordVisualTransformation(),
                onFocusChanged = { isFocused ->
                    if (!isFocused) {
                        viewModel.markFieldAsTouched("passwordConfirmation")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Number field with validation
            LabeledTextField(
                value = viewModel.numberValue,
                onValueChange = viewModel::updateNumber,
                label = "Number",
                hint = "Enter a whole number",
                errorMessage = fieldErrors["number"],
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
                singleLine = true,
                maxLines = 1,
                contentPadding = PaddingValues(
                    horizontal = 16.dp, vertical = 8.dp
                ),
                onFocusChanged = { isFocused ->
                    if (!isFocused) {
                        viewModel.markFieldAsTouched("number")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Bio field with validation
            LabeledTextField(
                value = viewModel.bio,
                onValueChange = viewModel::updateBio,
                label = "Short Bio",
                hint = "Tell us about yourself",
                errorMessage = fieldErrors["bio"],
                maxLines = 3,
                contentPadding = PaddingValues(
                    horizontal = 16.dp, vertical = 8.dp
                ),
                singleLine = false,
                onFocusChanged = { isFocused ->
                    if (!isFocused) {
                        viewModel.markFieldAsTouched("bio")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit button
        when (formSubmissionState) {
            is UiState.Loading -> {
                // Show loading indicator
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Success -> {
                // Show success state with reset button
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Form submitted successfully!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SecondaryButton(
                        text = "Reset Form",
                        onClick = viewModel::resetForm,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            else -> {
                // Show submit button
                PrimaryButton(
                    text = "Submit Form",
                    onClick = viewModel::submitForm,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Add some space at the bottom for better scrolling
        Spacer(modifier = Modifier.height(32.dp))
    }
}