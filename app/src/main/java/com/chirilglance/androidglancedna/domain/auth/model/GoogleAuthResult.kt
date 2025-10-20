package com.chirilglance.androidglancedna.domain.auth.model

/**
 * Result wrapper for Google authentication operations.
 *
 * This sealed class represents all possible outcomes of a Google sign-in attempt,
 * making error handling explicit and type-safe.
 */
sealed class GoogleAuthResult {
    /**
     * Authentication succeeded
     * @param user The authenticated user
     * @param token The authentication token (Firebase token or backend token depending on mode)
     * @param isNewUser Whether this is a new user registration (true) or returning user (false)
     */
    data class Success(
        val user: User,
        val token: String,
        val isNewUser: Boolean = false
    ) : GoogleAuthResult()

    /**
     * Authentication failed with an error
     * @param exception The exception that caused the failure
     * @param message User-friendly error message
     */
    data class Error(
        val exception: Exception,
        val message: String = exception.message ?: "Authentication failed"
    ) : GoogleAuthResult()

    /**
     * User cancelled the sign-in flow
     */
    object Cancelled : GoogleAuthResult()

    /**
     * Network error occurred during authentication
     * @param message Error message
     */
    data class NetworkError(
        val message: String = "Network error. Please check your connection."
    ) : GoogleAuthResult()
}