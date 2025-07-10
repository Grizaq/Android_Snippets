package com.chirilglance.androidglancedna.presentation.examples.auth.otpscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.data.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the OTP verification screen
 */
@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    // UI state
    private val _uiState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val uiState: StateFlow<UiState<Boolean>> = _uiState.asStateFlow()

    // Phone number state
    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    // OTP digits (4-digit code)
    private val _otpDigits = MutableStateFlow(List(4) { "" })
    val otpDigits: StateFlow<List<String>> = _otpDigits.asStateFlow()

    // Resend timer
    private val _remainingSeconds = MutableStateFlow(60)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    // Flag to track if verification is complete
    private var verificationCompleted = false

    /**
     * Initialize with phone number and start resend timer
     */
    fun initPhoneNumber(number: String) {
        _phoneNumber.update { number }
        startResendTimer()
    }

    /**
     * Update a specific OTP digit
     */
    fun updateOtpDigit(index: Int, digit: String) {
        if (index in _otpDigits.value.indices) {
            val updatedList = _otpDigits.value.toMutableList().apply {
                this[index] = digit
            }
            _otpDigits.update { updatedList }

            // Reset error state when input changes
            if (_uiState.value is UiState.Error) {
                _uiState.update { UiState.Empty }
            }
        }
    }

    /**
     * Verify the entered OTP code
     */
    fun verifyOtp(onSuccess: () -> Unit) {
        // Don't proceed if already verifying or completed
        if (_uiState.value is UiState.Loading || verificationCompleted) {
            return
        }

        // Don't proceed if not all digits are filled
        if (_otpDigits.value.any { it.isEmpty() }) {
            _uiState.update { UiState.Error("Please enter all 4 digits of the verification code") }
            return
        }

        // Get complete OTP
        val otp = _otpDigits.value.joinToString("")

        viewModelScope.launch {
            authRepository.verifyOtp(_phoneNumber.value, otp).collect { state ->
                _uiState.value = state

                // Handle successful verification
                if (state is UiState.Success) {
                    verificationCompleted = true
                    onSuccess()
                }
            }
        }
    }

    /**
     * Resend OTP code
     */
    fun resendOtp() {
        // Don't proceed if already loading
        if (_uiState.value is UiState.Loading) {
            return
        }

        // Reset error state
        if (_uiState.value is UiState.Error) {
            _uiState.update { UiState.Empty }
        }

        viewModelScope.launch {
            // Set loading state
            _uiState.update { UiState.Loading }

            try {
                // Simulate API call
                delay(1500)

                // Restart timer
                startResendTimer()

                // Reset OTP digits
                _otpDigits.update { List(4) { "" } }

                // Reset verification completed flag
                verificationCompleted = false

                // Reset state to empty
                _uiState.update { UiState.Empty }
            } catch (e: Exception) {
                // Handle error
                _uiState.update { UiState.Error(e.message ?: "Failed to resend code") }
            }
        }
    }

    /**
     * Start the resend timer countdown
     */
    private fun startResendTimer() {
        _remainingSeconds.update { 60 }

        viewModelScope.launch {
            while (_remainingSeconds.value > 0) {
                delay(1000)
                _remainingSeconds.update { it - 1 }
            }
        }
    }
}