package com.chirilglance.androidglancedna.presentation.examples.auth.otpscreen

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.SnackbarManager
import com.chirilglance.androidglancedna.data.repository.auth.AuthRepository
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the OTP verification screen with real Firebase integration
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

    // Firebase verification data
    private val _verificationId = MutableStateFlow("")
    val verificationId: StateFlow<String> = _verificationId.asStateFlow()

    private val _resendToken = MutableStateFlow<PhoneAuthProvider.ForceResendingToken?>(null)

    // OTP digits (6-digit code for Firebase)
    private val _otpDigits = MutableStateFlow(List(6) { "" })
    val otpDigits: StateFlow<List<String>> = _otpDigits.asStateFlow()

    // Resend timer
    private val _remainingSeconds = MutableStateFlow(60)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    // Flag to track if verification is complete
    private var verificationCompleted = false

    /**
     * Initialize with phone number, verification ID, and optional resend token
     */
    fun initVerification(
        number: String,
        verificationId: String,
        resendToken: PhoneAuthProvider.ForceResendingToken? = null
    ) {
        _phoneNumber.update { number }
        _verificationId.update { verificationId }
        _resendToken.update { resendToken }
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
     * Verify the entered OTP code with Firebase
     */
    fun verifyOtp(onSuccess: () -> Unit) {
        // Don't proceed if already verifying or completed
        if (_uiState.value is UiState.Loading || verificationCompleted) {
            return
        }

        // Don't proceed if not all digits are filled
        if (_otpDigits.value.any { it.isEmpty() }) {
            _uiState.update { UiState.Error("Please enter all 6 digits of the verification code") }
            SnackbarManager.showError("Please enter complete code")
            return
        }

        // Get complete OTP
        val otp = _otpDigits.value.joinToString("")
        val verificationId = _verificationId.value

        if (verificationId.isBlank()) {
            _uiState.update { UiState.Error("Verification ID missing. Please try again.") }
            SnackbarManager.showError("Verification error. Please restart.")
            return
        }

        viewModelScope.launch {
            authRepository.verifyOtp(verificationId, otp).collect { state ->
                _uiState.value = state

                // Handle successful verification
                when (state) {
                    is UiState.Success -> {
                        verificationCompleted = true
                        SnackbarManager.showSuccess("Phone verified successfully!")
                        onSuccess()
                    }

                    is UiState.Error -> {
                        SnackbarManager.showError(state.message)
                    }

                    else -> { /* No action needed */ }
                }
            }
        }
    }

    /**
     * Resend OTP code via Firebase
     */
    fun resendOtp(activity: Activity) {
        // Don't proceed if already loading
        if (_uiState.value is UiState.Loading) {
            return
        }

        // Reset error state
        if (_uiState.value is UiState.Error) {
            _uiState.update { UiState.Empty }
        }

        val phoneNumber = _phoneNumber.value
        val resendToken = _resendToken.value

        if (phoneNumber.isBlank()) {
            SnackbarManager.showError("Phone number missing. Please restart.")
            return
        }

        viewModelScope.launch {
            _uiState.update { UiState.Loading }

            try {
                if (resendToken != null) {
                    // Use resend token for faster resend
                    authRepository.resendVerificationCode(phoneNumber, activity, resendToken)
                        .collect { state ->
                            when (state) {
                                is UiState.Success -> {
                                    val result = state.data

                                    if (result.isAutoVerified) {
                                        // Auto-verification succeeded
                                        verificationCompleted = true
                                        SnackbarManager.showSuccess("Phone verified automatically!")
                                        _uiState.update { UiState.Success(true) }
                                    } else {
                                        // New code sent
                                        _verificationId.update { result.verificationId }
                                        _resendToken.update { result.resendToken }

                                        // Restart timer
                                        startResendTimer()

                                        // Reset OTP digits
                                        _otpDigits.update { List(6) { "" } }

                                        // Reset verification completed flag
                                        verificationCompleted = false

                                        _uiState.update { UiState.Empty }
                                        SnackbarManager.showSuccess("Code resent successfully")
                                    }
                                }

                                is UiState.Error -> {
                                    _uiState.update { UiState.Error(state.message) }
                                    SnackbarManager.showError(state.message)
                                }

                                else -> { /* Loading handled above */ }
                            }
                        }
                } else {
                    // No resend token, send new code
                    authRepository.sendVerificationCode(phoneNumber, activity)
                        .collect { state ->
                            when (state) {
                                is UiState.Success -> {
                                    val result = state.data

                                    _verificationId.update { result.verificationId }
                                    _resendToken.update { result.resendToken }

                                    startResendTimer()
                                    _otpDigits.update { List(6) { "" } }
                                    verificationCompleted = false

                                    _uiState.update { UiState.Empty }
                                    SnackbarManager.showSuccess("Code sent successfully")
                                }

                                is UiState.Error -> {
                                    _uiState.update { UiState.Error(state.message) }
                                    SnackbarManager.showError(state.message)
                                }

                                else -> { /* Loading handled above */ }
                            }
                        }
                }
            } catch (e: Exception) {
                _uiState.update { UiState.Error(e.message ?: "Failed to resend code") }
                SnackbarManager.showError("Failed to resend: ${e.message}")
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