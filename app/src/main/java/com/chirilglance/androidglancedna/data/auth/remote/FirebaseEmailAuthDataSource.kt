package com.chirilglance.androidglancedna.data.auth.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for Firebase Email/Password Authentication.
 *
 * Handles user registration and login with email and password.
 */
@Singleton
class FirebaseEmailAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    companion object {
        private const val TAG = "Auth"
    }

    /**
     * Register a new user with email and password.
     *
     * @param email User's email address
     * @param password User's password (min 6 characters)
     * @return User ID if successful, null otherwise
     */
    suspend fun registerWithEmail(email: String, password: String): EmailAuthResult {
        return try {
            Log.d(TAG, "Registering user with email: $email")

            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid

            if (userId != null) {
                Log.d(TAG, "Registration successful. UserId: $userId")
                EmailAuthResult.Success(userId, isNewUser = true)
            } else {
                Log.e(TAG, "Registration failed: No user ID returned")
                EmailAuthResult.Error("Registration failed. Please try again.")
            }
        } catch (e: FirebaseAuthWeakPasswordException) {
            Log.e(TAG, "Weak password: ${e.message}")
            EmailAuthResult.Error("Password is too weak. Use at least 6 characters.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e(TAG, "Invalid email: ${e.message}")
            EmailAuthResult.Error("Invalid email address format.")
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e(TAG, "Email already exists: ${e.message}")
            EmailAuthResult.Error("An account with this email already exists.")
        } catch (e: Exception) {
            Log.e(TAG, "Registration error: ${e.message}", e)
            EmailAuthResult.Error(e.message ?: "Registration failed. Please try again.")
        }
    }

    /**
     * Sign in an existing user with email and password.
     *
     * @param email User's email address
     * @param password User's password
     * @return User ID if successful, null otherwise
     */
    suspend fun signInWithEmail(email: String, password: String): EmailAuthResult {
        return try {
            Log.d(TAG, "Signing in with email: $email")

            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid

            if (userId != null) {
                Log.d(TAG, "Sign-in successful. UserId: $userId")
                EmailAuthResult.Success(userId, isNewUser = false)
            } else {
                Log.e(TAG, "Sign-in failed: No user ID returned")
                EmailAuthResult.Error("Sign-in failed. Please try again.")
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            Log.e(TAG, "User not found: ${e.message}")
            EmailAuthResult.Error("No account found with this email.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e(TAG, "Invalid credentials: ${e.message}")
            EmailAuthResult.Error("Invalid email or password.")
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in error: ${e.message}", e)
            EmailAuthResult.Error(e.message ?: "Sign-in failed. Please try again.")
        }
    }

    /**
     * Send password reset email.
     *
     * @param email User's email address
     * @return True if email sent successfully, false otherwise
     */
    suspend fun sendPasswordResetEmail(email: String): Boolean {
        return try {
            Log.d(TAG, "Sending password reset email to: $email")
            firebaseAuth.sendPasswordResetEmail(email).await()
            Log.d(TAG, "Password reset email sent successfully")
            true
        } catch (e: FirebaseAuthInvalidUserException) {
            Log.e(TAG, "User not found for password reset: ${e.message}")
            false
        } catch (e: Exception) {
            Log.e(TAG, "Password reset email error: ${e.message}", e)
            false
        }
    }

    /**
     * Sign out current user.
     */
    fun signOut() {
        try {
            firebaseAuth.signOut()
            Log.d(TAG, "User signed out")
        } catch (e: Exception) {
            Log.e(TAG, "Sign out error: ${e.message}", e)
        }
    }
}

/**
 * Result of email authentication operations.
 */
sealed class EmailAuthResult {
    data class Success(
        val userId: String,
        val isNewUser: Boolean
    ) : EmailAuthResult()

    data class Error(
        val message: String
    ) : EmailAuthResult()
}