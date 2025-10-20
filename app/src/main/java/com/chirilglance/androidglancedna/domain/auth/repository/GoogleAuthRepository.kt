package com.chirilglance.androidglancedna.domain.auth.repository

import android.content.Intent
import com.chirilglance.androidglancedna.domain.auth.model.GoogleAuthResult
import com.chirilglance.androidglancedna.domain.auth.model.User

/**
 * Repository interface for Google authentication operations.
 *
 * This interface abstracts the authentication logic and supports multiple modes:
 * - FIREBASE_ONLY: Direct Firebase authentication
 * - BACKEND_ONLY: Custom backend authentication
 * - FIREBASE_BACKEND: Hybrid approach (Firebase + backend token exchange)
 *
 * The actual mode is determined by AuthConfig at runtime.
 */
interface GoogleAuthRepository {

    /**
     * Get the Google Sign-In intent.
     * Launch this intent to show the Google account picker.
     *
     * @return Intent to launch Google Sign-In activity
     */
    fun getSignInIntent(): Intent

    /**
     * Complete the Google Sign-In flow with the result from the sign-in activity.
     *
     * This handles the full authentication flow based on the configured AuthMode:
     * - FIREBASE_ONLY: Sign in to Firebase, optionally save to Firestore, store Firebase token
     * - FIREBASE_BACKEND: Sign in to Firebase, exchange token with backend, store backend token
     * - BACKEND_ONLY: Not applicable for Google sign-in
     *
     * @param intent The result intent from the Google Sign-In activity
     * @return GoogleAuthResult indicating success, error, or cancellation
     */
    suspend fun signInWithGoogle(intent: Intent): GoogleAuthResult

    /**
     * Get the currently authenticated user.
     *
     * @return Current user or null if not authenticated
     */
    suspend fun getCurrentUser(): User?

    /**
     * Sign out the current user.
     *
     * This clears tokens and signs out from all services (Firebase, Google One Tap, etc.)
     */
    suspend fun signOut()

    /**
     * Check if a user is currently signed in.
     *
     * @return true if user is authenticated, false otherwise
     */
    fun isSignedIn(): Boolean
}