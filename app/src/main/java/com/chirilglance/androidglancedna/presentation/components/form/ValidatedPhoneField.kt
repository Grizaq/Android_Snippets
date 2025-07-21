package com.chirilglance.androidglancedna.presentation.components.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chirilglance.androidglancedna.core.ui.components.DefaultCountryCodeSelector
import com.chirilglance.androidglancedna.core.ui.components.DefaultPhoneField
import com.chirilglance.androidglancedna.core.validation.ValidationUtils
import com.chirilglance.androidglancedna.domain.models.CountryCode
import com.chirilglance.androidglancedna.domain.validators.PhoneNumberFormatter

/**
 * A validated phone number field with country code selector
 * Maintains the UI layout of the original design while adding validation
 */
@Composable
fun ValidatedPhoneField(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    selectedCountry: CountryCode,
    onCountrySelected: (CountryCode) -> Unit,
    phoneNumberValidator: PhoneNumberFormatter,
    modifier: Modifier = Modifier,
    initiallyValidated: Boolean = false,
    validateOnChange: Boolean = false,
    externalValidationTriggered: Boolean = false,
    onDone: (() -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Done,
    readOnly: Boolean = false
) {
    // Track whether this field has been interacted with
    var isInteracted by remember { mutableStateOf(initiallyValidated) }

    // Store validation result
    val validationResult = remember(phoneNumber, selectedCountry) {
        ValidationUtils.validatePhoneNumber(phoneNumber, selectedCountry)
    }

    // Determine if we should show an error
    val showError = (isInteracted || externalValidationTriggered) && !validationResult.isValid

    Column(modifier = modifier) {
        // Phone input row with country code and number field
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Country code selector
            DefaultCountryCodeSelector(
                selectedCountry = selectedCountry,
                onCountrySelected = {
                    onCountrySelected(it)
                    if (validateOnChange) isInteracted = true
                },
                modifier = Modifier.weight(0.35f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Phone number input field - already contains a Card wrapper
            DefaultPhoneField(
                value = phoneNumber,
                onValueChange = {
                    onPhoneNumberChange(it)
                    if (validateOnChange) isInteracted = true
                },
                isError = showError,
                onDone = { onDone?.invoke() },
                modifier = Modifier
                    .weight(0.7f)
                    .onFocusChanged { focusState ->
                        // Mark as interacted when focus is lost
                        if (!focusState.isFocused && focusState.hasFocus) {
                            isInteracted = true
                        }
                    }
            )
        }

        // Error message displayed below both inputs
        if (showError) {
            Text(
                text = validationResult.errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
    }
}