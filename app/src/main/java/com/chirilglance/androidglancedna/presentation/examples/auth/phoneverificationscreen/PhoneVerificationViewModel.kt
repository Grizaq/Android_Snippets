package com.chirilglance.androidglancedna.presentation.examples.auth.phoneverificationscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.data.repository.auth.AuthRepository
import com.chirilglance.androidglancedna.domain.models.CountryCode
import com.chirilglance.androidglancedna.domain.utils.CountryCodeProvider
import com.chirilglance.androidglancedna.domain.utils.PhoneNumberValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the phone verification screen
 */
@HiltViewModel
class PhoneVerificationViewModel @Inject constructor(
    private val phoneNumberValidator: PhoneNumberValidator,
    private val authRepository: AuthRepository
) : ViewModel() {
    // UI state
    private val _uiState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val uiState: StateFlow<UiState<String>> = _uiState.asStateFlow()

    // Phone number
    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    // Selected country
    private val _selectedCountry = MutableStateFlow(CountryCodeProvider.getDefaultCountry())
    val selectedCountry: StateFlow<CountryCode> = _selectedCountry.asStateFlow()

    // Phone number error
    private val _phoneNumberError = MutableStateFlow<String?>(null)
    val phoneNumberError: StateFlow<String?> = _phoneNumberError.asStateFlow()

    /**
     * Update phone number and validate
     */
    fun updatePhoneNumber(number: String) {
        _phoneNumber.update { number }
        validatePhoneNumber()
    }

    /**
     * Update selected country and validate
     */
    fun updateSelectedCountry(country: CountryCode) {
        _selectedCountry.update { country }
        validatePhoneNumber()
    }

    /**
     * Validate phone number format
     */
    private fun validatePhoneNumber() {
        val error = phoneNumberValidator.validatePhoneNumber(
            phoneNumber.value,
            selectedCountry.value
        )
        _phoneNumberError.update { error }

        // Reset any API error message when user changes input
        if (_uiState.value is UiState.Error) {
            _uiState.update { UiState.Empty }
        }
    }

    /**
     * Send verification request to API
     */
    fun verifyPhoneNumber(onSuccess: (String) -> Unit) {
        // Validate phone number first
        validatePhoneNumber()
        if (phoneNumberError.value != null) {
            return
        }

        // Get formatted number for API
        val formattedNumber = getFormattedPhoneNumber()

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
        return phoneNumberValidator.formatForApi(phoneNumber.value, selectedCountry.value)
    }

    /**
     * Get formatted phone number for display
     */
    fun getFormattedPhoneNumberForDisplay(): String {
        return phoneNumberValidator.formatForDisplay(phoneNumber.value, selectedCountry.value)
    }

    /**
     * Check if phone number has reached the expected length for the selected country
     */
    fun isPhoneNumberComplete(): Boolean {
        // No need to manually clean the number and check against regex here
        // Just use the validator to check if there are any errors
        return phoneNumberValidator.validatePhoneNumber(phoneNumber.value, selectedCountry.value) == null &&
                phoneNumber.value.isNotBlank()
    }

    /**
     * Check if the phone number can be verified (valid and not empty)
     */
    fun canVerify(): Boolean {
        return phoneNumber.value.isNotBlank() && phoneNumberError.value == null
    }
}