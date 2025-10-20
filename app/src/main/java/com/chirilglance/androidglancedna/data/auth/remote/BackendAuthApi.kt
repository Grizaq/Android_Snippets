package com.chirilglance.androidglancedna.data.auth.remote

import com.chirilglance.androidglancedna.domain.auth.model.User

/**
 * Interface for backend authentication API.
 *
 * This defines the contract for communicating with your custom backend.
 * Implement this interface when you have a backend ready.
 *
 * For now, this is used in BACKEND_ONLY and FIREBASE_BACKEND modes.
 */
interface BackendAuthApi {

    /**
     * Exchange Google ID token for backend authentication token.
     * Used in FIREBASE_BACKEND mode.
     *
     * @param googleIdToken The Google ID token from Firebase
     * @return Pair of (User, Backend Token), or null if exchange fails
     *
     * Example backend endpoint:
     * POST /api/auth/google
     * Body: { "idToken": "..." }
     * Response: { "token": "backend-jwt", "user": {...} }
     */
    suspend fun exchangeGoogleToken(googleIdToken: String): Pair<User, String>?

    /**
     * Traditional email/password login.
     * Used in BACKEND_ONLY mode.
     *
     * @param email User's email
     * @param password User's password
     * @return Pair of (User, Backend Token), or null if login fails
     *
     * Example backend endpoint:
     * POST /api/auth/login
     * Body: { "email": "...", "password": "..." }
     * Response: { "token": "backend-jwt", "user": {...} }
     */
    suspend fun login(email: String, password: String): Pair<User, String>?

    /**
     * Phone number authentication.
     * Used in BACKEND_ONLY mode.
     *
     * @param phoneNumber User's phone number
     * @param verificationCode OTP code
     * @return Pair of (User, Backend Token), or null if verification fails
     *
     * Example backend endpoint:
     * POST /api/auth/verify-phone
     * Body: { "phoneNumber": "...", "code": "..." }
     * Response: { "token": "backend-jwt", "user": {...} }
     */
    suspend fun verifyPhone(phoneNumber: String, verificationCode: String): Pair<User, String>?

    /**
     * Refresh authentication token.
     *
     * @param refreshToken The refresh token
     * @return New access token, or null if refresh fails
     *
     * Example backend endpoint:
     * POST /api/auth/refresh
     * Body: { "refreshToken": "..." }
     * Response: { "token": "new-backend-jwt" }
     */
    suspend fun refreshToken(refreshToken: String): String?
}

/**
 * Mock implementation for testing and development.
 * Replace with real implementation when backend is ready.
 */
class MockBackendAuthApi : BackendAuthApi {

    override suspend fun exchangeGoogleToken(googleIdToken: String): Pair<User, String>? {
        // TODO: Replace with actual API call
        // For now, return null to simulate "not implemented"
        return null
    }

    override suspend fun login(email: String, password: String): Pair<User, String>? {
        // TODO: Replace with actual API call
        return null
    }

    override suspend fun verifyPhone(phoneNumber: String, verificationCode: String): Pair<User, String>? {
        // TODO: Replace with actual API call
        return null
    }

    override suspend fun refreshToken(refreshToken: String): String? {
        // TODO: Replace with actual API call
        return null
    }
}