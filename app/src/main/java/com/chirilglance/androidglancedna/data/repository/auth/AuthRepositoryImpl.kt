package com.chirilglance.androidglancedna.data.repository.auth

import android.app.Activity
import android.util.Log
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.data.auth.config.AuthConfig
import com.chirilglance.androidglancedna.data.auth.remote.FirebaseDataSource
import com.chirilglance.androidglancedna.data.auth.remote.FirebasePhoneAuthDataSource
import com.chirilglance.androidglancedna.data.auth.remote.PhoneAuthEvent
import com.chirilglance.androidglancedna.domain.auth.TokenManager
import com.chirilglance.androidglancedna.domain.auth.model.User
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

/**
 * Firebase implementation of AuthRepository.
 *
 * Handles phone authentication using Firebase Phone Auth.
 */
class AuthRepositoryImpl @Inject constructor(
    private val firebasePhoneAuthDataSource: FirebasePhoneAuthDataSource,
    private val firebaseDataSource: FirebaseDataSource,
    private val tokenManager: TokenManager
) : AuthRepository {

    companion object {
        private const val TAG = "Auth"
    }

    override fun sendVerificationCode(
        phoneNumber: String,
        activity: Activity
    ): Flow<UiState<VerificationResult>> {
        return firebasePhoneAuthDataSource.sendVerificationCode(phoneNumber, activity)
            .map { event ->
                when (event) {
                    is PhoneAuthEvent.CodeSent -> {
                        Log.d(TAG, "Code sent successfully")
                        UiState.Success(
                            VerificationResult(
                                verificationId = event.verificationId,
                                resendToken = event.resendToken,
                                isAutoVerified = false
                            )
                        )
                    }

                    is PhoneAuthEvent.VerificationCompleted -> {
                        // Auto-verification succeeded (instant verification)
                        Log.d(TAG, "Auto-verification completed")

                        // Sign in with credential
                        val userId = firebasePhoneAuthDataSource.signInWithCredential(event.credential)

                        if (userId != null) {
                            // Save user to Firestore
                            handlePhoneSignInSuccess(userId, phoneNumber)

                            UiState.Success(
                                VerificationResult(
                                    verificationId = "",
                                    resendToken = null,
                                    isAutoVerified = true
                                )
                            )
                        } else {
                            UiState.Error("Auto-verification failed")
                        }
                    }

                    is PhoneAuthEvent.VerificationFailed -> {
                        Log.e(TAG, "Verification failed: ${event.error}")
                        UiState.Error(event.error)
                    }
                }
            }
            .onStart {
                emit(UiState.Loading)
            }
            .catch { e ->
                Log.e(TAG, "Error in sendVerificationCode: ${e.message}", e)
                emit(UiState.Error(e.message ?: "Failed to send verification code"))
            }
    }

    override fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        resendToken: PhoneAuthProvider.ForceResendingToken
    ): Flow<UiState<VerificationResult>> {
        return firebasePhoneAuthDataSource.resendVerificationCode(phoneNumber, activity, resendToken)
            .map { event ->
                when (event) {
                    is PhoneAuthEvent.CodeSent -> {
                        Log.d(TAG, "Code resent successfully")
                        UiState.Success(
                            VerificationResult(
                                verificationId = event.verificationId,
                                resendToken = event.resendToken,
                                isAutoVerified = false
                            )
                        )
                    }

                    is PhoneAuthEvent.VerificationCompleted -> {
                        Log.d(TAG, "Auto-verification completed on resend")

                        val userId = firebasePhoneAuthDataSource.signInWithCredential(event.credential)

                        if (userId != null) {
                            handlePhoneSignInSuccess(userId, phoneNumber)

                            UiState.Success(
                                VerificationResult(
                                    verificationId = "",
                                    resendToken = null,
                                    isAutoVerified = true
                                )
                            )
                        } else {
                            UiState.Error("Auto-verification failed")
                        }
                    }

                    is PhoneAuthEvent.VerificationFailed -> {
                        Log.e(TAG, "Verification failed on resend: ${event.error}")
                        UiState.Error(event.error)
                    }
                }
            }
            .onStart {
                emit(UiState.Loading)
            }
            .catch { e ->
                Log.e(TAG, "Error in resendVerificationCode: ${e.message}", e)
                emit(UiState.Error(e.message ?: "Failed to resend verification code"))
            }
    }

    override suspend fun verifyOtp(
        verificationId: String,
        code: String
    ): Flow<UiState<Boolean>> = flow {
        emit(UiState.Loading)

        try {
            // Create credential with verification ID and code
            val credential = firebasePhoneAuthDataSource.verifyCode(verificationId, code)

            // Sign in with credential
            val userId = firebasePhoneAuthDataSource.signInWithCredential(credential)

            if (userId != null) {
                Log.d(TAG, "OTP verification successful for user: $userId")

                // Get Firebase token
                val token = firebaseDataSource.getIdToken(forceRefresh = false)

                if (token != null) {
                    // Save token
                    tokenManager.saveToken(token)

                    // Get phone number from Firebase user
                    val user = firebaseDataSource.getCurrentFirebaseUser()
                    val phoneNumber = user?.phoneNumber

                    // Save user to Firestore if enabled
                    if (phoneNumber != null) {
                        handlePhoneSignInSuccess(userId, phoneNumber)
                    }

                    emit(UiState.Success(true))
                } else {
                    Log.e(TAG, "Failed to get Firebase token")
                    emit(UiState.Error("Failed to get authentication token"))
                }
            } else {
                Log.e(TAG, "OTP verification failed: No user ID")
                emit(UiState.Error("Invalid verification code. Please try again."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "OTP verification error: ${e.message}", e)
            emit(UiState.Error(e.message ?: "Verification failed"))
        }
    }

    /**
     * Handle successful phone sign-in by saving user to Firestore
     */
    private suspend fun handlePhoneSignInSuccess(userId: String, phoneNumber: String) {
        if (AuthConfig.STORE_IN_FIRESTORE) {
            try {
                val user = User(
                    id = userId,
                    phoneNumber = phoneNumber,
                    role = "user"
                )

                // Check if user exists in Firestore
                val userExists = firebaseDataSource.getUserFromFirestore(userId) != null

                // Save to Firestore
                firebaseDataSource.saveUserToFirestore(user, isNewUser = !userExists)

                Log.d(TAG, "User saved to Firestore: $userId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save user to Firestore: ${e.message}", e)
                // Don't fail auth if Firestore save fails
            }
        }
    }
}