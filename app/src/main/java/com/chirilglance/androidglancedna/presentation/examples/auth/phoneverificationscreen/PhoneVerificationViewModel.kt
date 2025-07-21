package com.chirilglance.androidglancedna.presentation.examples.auth.phoneverificationscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.validation.ValidationResult
import com.chirilglance.androidglancedna.core.validation.ValidationUtils
import com.chirilglance.androidglancedna.data.repository.auth.AuthRepository
import com.chirilglance.androidglancedna.domain.models.CountryCode
import com.chirilglance.androidglancedna.domain.utils.CountryCodeProvider
import com.chirilglance.androidglancedna.domain.validators.PhoneNumberFormatter
import com.chirilglance.androidglancedna.presentation.components.form.FormValidationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneVerificationViewModel @Inject constructor(
    val phoneNumberValidator: PhoneNumberFormatter,
    private val authRepository: AuthRepository
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val uiState: StateFlow<UiState<String>> = _uiState.asStateFlow()

    // Form fields - Using mutableStateOf for better compose integration
    var phoneNumber by mutableStateOf("")
        private set

    var selectedCountry by mutableStateOf(CountryCodeProvider.getDefaultCountry())
        private set

    // Form validation manager
    val formValidator = FormValidationManager(
        { validatePhone(phoneNumber) }
    )

    // Update methods
    fun updatePhoneNumber(number: String) {
        phoneNumber = number
        // Reset any API error message when user changes input
        if (_uiState.value is UiState.Error) {
            _uiState.update { UiState.Empty }
        }
    }

    fun updateSelectedCountry(country: CountryCode) {
        selectedCountry = country
        // Reset any API error message when user changes input
        if (_uiState.value is UiState.Error) {
            _uiState.update { UiState.Empty }
        }
    }

    // Validation methods
    fun validatePhone(value: String): ValidationResult {
        return ValidationUtils.validatePhoneNumber(value, selectedCountry)
    }

    /**
     * Send verification request to API
     */
    fun verifyPhoneNumber(onSuccess: (String) -> Unit) {
        // Validate phone number first
        if (!formValidator.validateAll()) {
            return
        }

        // Get formatted number for API
        val formattedNumber = getFormattedPhoneNumber()

        // Set loading state
        _uiState.update { UiState.Loading }

        viewModelScope.launch {
            authRepository.verifyPhoneNumber(formattedNumber).collect { state ->
                _uiState.value = state

                // Handle successful verification
                if (state is UiState.Success) {
                    onSuccess(formattedNumber)
                }
            }
        }
    }

    /**
     * Get formatted phone number for API use
     */
    fun getFormattedPhoneNumber(): String {
        return phoneNumberValidator.formatForApi(phoneNumber, selectedCountry)
    }

    /**
     * Get formatted phone number for display
     */
    fun getFormattedPhoneNumberForDisplay(): String {
        return phoneNumberValidator.formatForDisplay(phoneNumber, selectedCountry)
    }

    /**
     * Check if phone number has reached the expected length for the selected country
     */
    fun isPhoneNumberComplete(): Boolean {
        return validatePhone(phoneNumber).isValid && phoneNumber.isNotBlank()
    }

    /**
     * Check if the phone number can be verified (valid and not empty)
     */
    fun canVerify(): Boolean {
        return phoneNumber.isNotBlank() && validatePhone(phoneNumber).isValid
    }
}