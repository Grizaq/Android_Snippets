package com.chirilglance.androidglancedna.presentation.examples.forms

import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.LabeledTextField
import com.chirilglance.androidglancedna.core.ui.components.buttons.PrimaryButton
import com.chirilglance.androidglancedna.core.ui.components.buttons.SecondaryButton
import com.chirilglance.androidglancedna.presentation.components.form.DateOfBirthField
import com.chirilglance.androidglancedna.presentation.components.form.EventDateField
import com.chirilglance.androidglancedna.presentation.components.form.ValidatedPhoneField
import com.chirilglance.androidglancedna.presentation.ui.theme.ClubConnectGreen

@Composable
fun FormValidationScreen(
    viewModel: FormValidationViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    val formSubmissionState by viewModel.formSubmissionState.collectAsState()
    val isValidated = viewModel.formValidator.isValidated.value

    // Track currently focused field
    var focusedField by remember { mutableStateOf("") }

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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
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

                // Full Name field with validation and elevation
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (focusedField == "fullName")
                            ClubConnectGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (focusedField == "fullName") {
                                Modifier.border(
                                    width = 1.dp,
                                    color = ClubConnectGreen,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            } else {
                                Modifier
                            }
                        )
                ) {
                    Column {
                        LabeledTextField(
                            value = viewModel.fullName,
                            onValueChange = viewModel::updateFullName,
                            label = "Full Name",
                            hint = "Enter your full name",
                            errorMessage = if (viewModel.isFullNameValidated || isValidated)
                                viewModel.validateFullName(viewModel.fullName).errorMessage else null,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                            singleLine = true,
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    focusedField = if (focusState.isFocused) "fullName" else ""
                                    if (!focusState.isFocused && focusState.hasFocus) {
                                        viewModel.validateFullNameField()
                                    }
                                }
                        )
                    }
                }

                // Email field with validation and elevation
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (focusedField == "email")
                            ClubConnectGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (focusedField == "email") {
                                Modifier.border(
                                    width = 1.dp,
                                    color = ClubConnectGreen,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            } else {
                                Modifier
                            }
                        )
                ) {
                    Column {
                        LabeledTextField(
                            value = viewModel.email,
                            onValueChange = viewModel::updateEmail,
                            label = "Email Address",
                            hint = "your@email.com",
                            errorMessage = if (viewModel.isEmailValidated || isValidated)
                                viewModel.validateEmail(viewModel.email).errorMessage else null,
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                            singleLine = true,
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    focusedField = if (focusState.isFocused) "email" else ""
                                    if (!focusState.isFocused && focusState.hasFocus) {
                                        viewModel.validateEmailField()
                                    }
                                }
                        )
                    }
                }

                // Phone field with validation - consistent with auth flow
                ValidatedPhoneField(
                    phoneNumber = viewModel.phoneNumber,
                    onPhoneNumberChange = viewModel::updatePhoneNumber,
                    selectedCountry = viewModel.selectedCountry,
                    onCountrySelected = viewModel::updateSelectedCountry,
                    phoneNumberValidator = viewModel.phoneNumberValidator,
                    externalValidationTriggered = viewModel.isPhoneValidated || isValidated,
                    onDone = null,
                    initiallyValidated = false,
                    validateOnChange = true
                )

                // Date of Birth field with validation
                DateOfBirthField(
                    selectedDate = viewModel.birthDate,
                    onDateSelected = viewModel::updateBirthDate,
                    errorMessage = if (viewModel.isBirthDateValidated || isValidated)
                        viewModel.validateBirthDate().errorMessage else null,
                    modifier = Modifier.fillMaxWidth(),
                    isFocused = focusedField == "birthDate"
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
                    errorMessage = if (viewModel.isEventDateValidated || isValidated)
                        viewModel.validateEventDate().errorMessage else null,
                    label = "Event Date & Time",
                    hint = "Select event date and time",
                    modifier = Modifier.fillMaxWidth(),
                    allowPastDates = false,
                    isFocused = focusedField == "eventDate"
                )

                // Password field with validation
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (focusedField == "password")
                            ClubConnectGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (focusedField == "password") {
                                Modifier.border(
                                    width = 1.dp,
                                    color = ClubConnectGreen,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            } else {
                                Modifier
                            }
                        )
                ) {
                    Column {
                        LabeledTextField(
                            value = viewModel.password,
                            onValueChange = viewModel::updatePassword,
                            label = "Password",
                            hint = "Enter a secure password",
                            errorMessage = if (viewModel.isPasswordValidated || isValidated)
                                viewModel.validatePassword(viewModel.password).errorMessage else null,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next,
                            singleLine = true,
                            maxLines = 1,
                            contentPadding = PaddingValues(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    focusedField = if (focusState.isFocused) "password" else ""
                                    if (!focusState.isFocused && focusState.hasFocus) {
                                        viewModel.validatePasswordField()
                                    }
                                }
                        )
                    }
                }

                // Password confirmation field
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (focusedField == "passwordConfirmation")
                            ClubConnectGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (focusedField == "passwordConfirmation") {
                                Modifier.border(
                                    width = 1.dp,
                                    color = ClubConnectGreen,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            } else {
                                Modifier
                            }
                        )
                ) {
                    Column {
                        LabeledTextField(
                            value = viewModel.passwordConfirmation,
                            onValueChange = viewModel::updatePasswordConfirmation,
                            label = "Confirm Password",
                            hint = "Re-enter your password",
                            errorMessage = if (viewModel.isPasswordConfirmationValidated || isValidated)
                                viewModel.validatePasswordConfirmation(
                                    viewModel.password,
                                    viewModel.passwordConfirmation
                                ).errorMessage else null,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next,
                            singleLine = true,
                            maxLines = 1,
                            contentPadding = PaddingValues(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    focusedField = if (focusState.isFocused) "passwordConfirmation" else ""
                                    if (!focusState.isFocused && focusState.hasFocus) {
                                        viewModel.validatePasswordConfirmationField()
                                    }
                                }
                        )
                    }
                }


                // Number field with validation

                LabeledTextField(
                    value = viewModel.numberValue,
                    onValueChange = viewModel::updateNumber,
                    label = "Number",
                    hint = "Enter a whole number",
                    errorMessage = if (viewModel.isNumberValidated || isValidated)
                        viewModel.validateNumber(viewModel.numberValue).errorMessage else null,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                    singleLine = true,
                    maxLines = 1,
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            focusedField = if (focusState.isFocused) "number" else ""
                            if (!focusState.isFocused && focusState.hasFocus) {
                                viewModel.validateNumberField()
                            }
                        }
                )




                // Bio field with validation and elevation
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (focusedField == "bio")
                            ClubConnectGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (focusedField == "bio") {
                                Modifier.border(
                                    width = 1.dp,
                                    color = ClubConnectGreen,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            } else {
                                Modifier
                            }
                        )
                ) {
                    Column {
                        LabeledTextField(
                            value = viewModel.bio,
                            onValueChange = viewModel::updateBio,
                            label = "Short Bio",
                            hint = "Tell us about yourself",
                            errorMessage = if (viewModel.isBioValidated || isValidated)
                                viewModel.validateBio(viewModel.bio).errorMessage else null,
                            maxLines = 3,
                            contentPadding = PaddingValues(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                            singleLine = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    focusedField = if (focusState.isFocused) "bio" else ""
                                    if (!focusState.isFocused && focusState.hasFocus) {
                                        viewModel.validateBioField()
                                    }
                                }
                        )
                    }
                }

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
}
