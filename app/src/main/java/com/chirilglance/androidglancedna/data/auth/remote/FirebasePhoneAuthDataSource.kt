package com.chirilglance.androidglancedna.data.auth.remote

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for Firebase Phone Authentication.
 *
 * Handles SMS OTP verification using Firebase Phone Auth.
 */
@Singleton
class FirebasePhoneAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    companion object {
        private const val TAG = "Auth"
        private const val TIMEOUT_SECONDS = 60L
    }

    /**
     * Send verification code to phone number.
     *
     * Returns a Flow that emits verification events:
     * - CodeSent: SMS sent successfully with verificationId
     * - VerificationCompleted: Auto-verification succeeded (instant verification)
     * - VerificationFailed: SMS sending failed
     *
     * @param phoneNumber Phone number in E.164 format (e.g., +447911123456)
     * @param activity Activity context needed for Firebase Phone Auth
     */
    fun sendVerificationCode(
        phoneNumber: String,
        activity: Activity
    ): Flow<PhoneAuthEvent> = callbackFlow {
        Log.d(TAG, "Sending verification code to: $phoneNumber")

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Auto-verification succeeded (instant verification on some devices)
                Log.d(TAG, "Phone verification completed automatically")
                trySend(PhoneAuthEvent.VerificationCompleted(credential))
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e(TAG, "Phone verification failed: ${e.message}", e)
                trySend(PhoneAuthEvent.VerificationFailed(e.message ?: "Verification failed"))
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "Verification code sent. VerificationId: $verificationId")
                trySend(PhoneAuthEvent.CodeSent(verificationId, token))
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose {
            Log.d(TAG, "Phone auth flow closed")
        }
    }

    /**
     * Resend verification code with force resending token.
     *
     * @param phoneNumber Phone number in E.164 format
     * @param activity Activity context
     * @param resendToken Token from previous code sent event
     */
    fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        resendToken: PhoneAuthProvider.ForceResendingToken
    ): Flow<PhoneAuthEvent> = callbackFlow {
        Log.d(TAG, "Resending verification code to: $phoneNumber")

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "Phone verification completed automatically (resend)")
                trySend(PhoneAuthEvent.VerificationCompleted(credential))
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e(TAG, "Phone verification failed (resend): ${e.message}", e)
                trySend(PhoneAuthEvent.VerificationFailed(e.message ?: "Verification failed"))
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "Verification code resent. VerificationId: $verificationId")
                trySend(PhoneAuthEvent.CodeSent(verificationId, token))
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose {
            Log.d(TAG, "Phone auth resend flow closed")
        }
    }

    /**
     * Verify the OTP code entered by user.
     *
     * @param verificationId The verification ID from code sent event
     * @param code The SMS code entered by user
     * @return PhoneAuthCredential to sign in with Firebase
     */
    fun verifyCode(verificationId: String, code: String): PhoneAuthCredential {
        Log.d(TAG, "Verifying code for verificationId: $verificationId")
        return PhoneAuthProvider.getCredential(verificationId, code)
    }

    /**
     * Sign in to Firebase with phone credential.
     *
     * @param credential Phone auth credential from verification
     * @return User ID if successful, null otherwise
     */
    suspend fun signInWithCredential(credential: PhoneAuthCredential): String? {
        return try {
            Log.d(TAG, "Signing in with phone credential...")
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val userId = authResult.user?.uid

            if (userId != null) {
                Log.d(TAG, "Phone sign-in successful. UserId: $userId")
            } else {
                Log.e(TAG, "Phone sign-in failed: No user ID returned")
            }

            userId
        } catch (e: Exception) {
            Log.e(TAG, "Phone sign-in failed: ${e.message}", e)
            null
        }
    }
}

/**
 * Sealed class representing phone authentication events.
 */
sealed class PhoneAuthEvent {
    /**
     * Verification code sent successfully via SMS
     */
    data class CodeSent(
        val verificationId: String,
        val resendToken: PhoneAuthProvider.ForceResendingToken
    ) : PhoneAuthEvent()

    /**
     * Auto-verification completed (instant verification)
     */
    data class VerificationCompleted(
        val credential: PhoneAuthCredential
    ) : PhoneAuthEvent()

    /**
     * Verification failed with error
     */
    data class VerificationFailed(
        val error: String
    ) : PhoneAuthEvent()
}