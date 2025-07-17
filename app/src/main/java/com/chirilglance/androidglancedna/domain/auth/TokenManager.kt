package com.chirilglance.androidglancedna.domain.auth

/**
 * Interface for managing authentication tokens.
 * This provides a clean abstraction for token operations regardless of
 * the underlying storage or token type.
 */
interface TokenManager {
    /**
     * Saves an authentication token
     * @param token The token to save
     */
    suspend fun saveToken(token: String)

    /**
     * Retrieves the current authentication token if available
     * @return The current token or null if not authenticated
     */
    suspend fun getToken(): String?

    /**
     * Clears all authentication tokens and related data
     */
    suspend fun clearToken()

    /**
     * Checks if the user is currently logged in
     * @return true if the user has a valid token, false otherwise
     */
    fun isLoggedIn(): Boolean

    /**
     * Checks if the current token is expired
     * @return true if token is expired or doesn't exist, false if valid
     */
    suspend fun isTokenExpired(): Boolean

    /**
     * Checks if the token will expire soon (within the buffer time)
     * @return true if token will expire soon
     */
    suspend fun isTokenExpiringSoon(): Boolean

    /**
     * Saves both access and refresh tokens
     * @param accessToken The access token
     * @param refreshToken The refresh token
     */
    suspend fun saveTokens(accessToken: String, refreshToken: String)

    /**
     * Gets the refresh token if available
     * @return The refresh token or null
     */
    suspend fun getRefreshToken(): String?

    /**
     * Updates only the access token while keeping the refresh token
     * @param newAccessToken The new access token
     */
    suspend fun updateAccessToken(newAccessToken: String)
}