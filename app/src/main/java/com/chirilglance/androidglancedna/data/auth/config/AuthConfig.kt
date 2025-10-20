package com.chirilglance.androidglancedna.data.auth.config

import com.chirilglance.androidglancedna.domain.auth.model.AuthMode

/**
 * Runtime configuration for authentication behavior.
 *
 * Change these values to switch between different auth modes without rebuilding.
 * This makes it easy to adapt GlanceDNA for different project requirements.
 */
object AuthConfig {
    /**
     * Current authentication mode
     *
     * - FIREBASE_ONLY: Quick setup, uses Google OAuth → Firebase (no backend needed)
     * - BACKEND_ONLY: Traditional auth with your custom backend (no Firebase/Google)
     * - FIREBASE_BACKEND: Hybrid approach - Google OAuth → Firebase → Exchange token with backend
     */
    val authMode: AuthMode = AuthMode.FIREBASE_ONLY

    /**
     * Whether to store user data in Firestore after authentication
     * Recommended: true for production apps, false for demo/testing
     */
    const val STORE_IN_FIRESTORE = true

    /**
     * Firestore collection name for users
     */
    const val USERS_COLLECTION = "users"
}