package com.chirilglance.androidglancedna.presentation.examples.auth.phoneverificationscreen

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.SnackbarManager
import com.chirilglance.androidglancedna.core.validation.ValidationResult
import com.chirilglance.androidglancedna.core.validation.ValidationUtils
import com.chirilglance.androidglancedna.data.repository.auth.AuthRepository
import com.chirilglance.androidglancedna.domain.models.CountryCode
import com.chirilglance.androidglancedna.domain.utils.CountryCodeProvider
import com.chirilglance.androidglancedna.domain.validators.PhoneNumberFormatter
import com.chirilglance.androidglancedna.presentation.components.form.FormValidationManager
import com.google.firebase.auth.PhoneAuthProvider
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

    // Form fields
    var phoneNumber by mutableStateOf("")
        private set

    var selectedCountry by mutableStateOf(CountryCodeProvider.getDefaultCountry())
        private set

    // Firebase verification data
    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId.asStateFlow()

    private val _resendToken = MutableStateFlow<PhoneAuthProvider.ForceResendingToken?>(null)
    val resendToken: StateFlow<PhoneAuthProvider.ForceResendingToken?> = _resendToken.asStateFlow()

    // Form validation manager
    val formValidator = FormValidationManager(
        { validatePhone(phoneNumber) }
    )

    // Update methods
    fun updatePhoneNumber(number: String) {
        phoneNumber = number
        if (_uiState.value is UiState.Error) {
            _uiState.update { UiState.Empty }
        }
    }

    fun updateSelectedCountry(country: CountryCode) {
        selectedCountry = country
        if (_uiState.value is UiState.Error) {
            _uiState.update { UiState.Empty }
        }
    }

    // Validation methods
    fun validatePhone(value: String): ValidationResult {
        return ValidationUtils.validatePhoneNumber(value, selectedCountry)
    }

    /**
     * Send verification code via Firebase Phone Auth.
     *
     * @param activity Activity context required by Firebase
     * @param onSuccess Callback with formatted phone number and verification ID
     */
    fun sendVerificationCode(
        activity: Activity,
        onSuccess: (String, String) -> Unit
    ) {
        // Validate phone number first
        if (!formValidator.validateAll()) {
            return
        }

        // Get formatted number for Firebase (E.164 format)
        val formattedNumber = phoneNumberValidator.formatForApi(phoneNumber, selectedCountry)

        viewModelScope.launch {
            authRepository.sendVerificationCode(formattedNumber, activity).collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        _uiState.update { UiState.Loading }
                    }

                    is UiState.Success -> {
                        val result = state.data

                        if (result.isAutoVerified) {
                            // Auto-verification succeeded - user is signed in
                            SnackbarManager.showSuccess("Phone verified automatically!")
                            // Navigate to home or next screen
                            _uiState.update { UiState.Success(formattedNumber) }
                        } else {
                            // Code sent - save verification data
                            _verificationId.update { result.verificationId }
                            _resendToken.update { result.resendToken }

                            _uiState.update { UiState.Success(formattedNumber) }
                            onSuccess(formattedNumber, result.verificationId)
                        }
                    }

                    is UiState.Error -> {
                        _uiState.update { UiState.Error(state.message) }
                        SnackbarManager.showError(state.message)
                    }

                    is UiState.Empty -> {
                        _uiState.update { UiState.Empty }
                    }
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