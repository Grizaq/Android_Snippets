package com.chirilglance.androidglancedna.data.repository.auth

import android.app.Activity
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations.
 *
 * Handles both phone and email authentication using Firebase.
 */
interface AuthRepository {

    /**
     * Send verification SMS to phone number.
     *
     * @param phoneNumber Phone number in E.164 format (e.g., +447911123456)
     * @param activity Activity context required by Firebase
     * @return Flow emitting UI states for the verification process
     */
    fun sendVerificationCode(
        phoneNumber: String,
        activity: Activity
    ): Flow<UiState<VerificationResult>>

    /**
     * Resend verification code.
     *
     * @param phoneNumber Phone number in E.164 format
     * @param activity Activity context
     * @param resendToken Token from previous code sent event
     * @return Flow emitting UI states for the resend process
     */
    fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        resendToken: PhoneAuthProvider.ForceResendingToken
    ): Flow<UiState<VerificationResult>>

    /**
     * Verify the OTP code entered by user.
     *
     * @param verificationId The verification ID from SMS sent event
     * @param code The 6-digit code entered by user
     * @return Flow emitting UI states for verification
     */
    suspend fun verifyOtp(
        verificationId: String,
        code: String
    ): Flow<UiState<Boolean>>
}

/**
 * Result of phone verification code sending.
 */
data class VerificationResult(
    val verificationId: String,
    val resendToken: PhoneAuthProvider.ForceResendingToken? = null,
    val isAutoVerified: Boolean = false
)