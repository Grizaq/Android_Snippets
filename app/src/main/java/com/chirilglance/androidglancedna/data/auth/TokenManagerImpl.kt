package com.chirilglance.androidglancedna.data.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.chirilglance.androidglancedna.core.data.storage.SecureStorageManager
import com.chirilglance.androidglancedna.domain.auth.TokenManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of TokenManager that uses secure encrypted storage.
 */
@Singleton
class TokenManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val secureStorageManager: SecureStorageManager
) : TokenManager {

    // Create the encrypted shared preferences
    private val sharedPreferences: SharedPreferences by lazy {
        secureStorageManager.createEncryptedPreferences("encrypted_token_preferences")
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRATION = "token_expiration_time"
        private const val KEY_REFRESH_USED = "refresh_token_used"
        private const val TAG = "TokenManager"

        // Default expiration time for access token (24 hours in milliseconds)
        private const val DEFAULT_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000L

        // Buffer time before expiration to refresh (1 hour in milliseconds)
        private const val REFRESH_BUFFER_TIME = 60 * 60 * 1000L
    }

    override suspend fun saveToken(token: String) {
        withContext(Dispatchers.IO) {
            try {
                // Calculate expiration time (24 hours from now)
                val expirationTime = System.currentTimeMillis() + DEFAULT_TOKEN_EXPIRATION

                secureStorageManager.safeStringWrite(sharedPreferences, KEY_ACCESS_TOKEN, token)
                secureStorageManager.safeLongWrite(sharedPreferences, KEY_TOKEN_EXPIRATION, expirationTime)

                Log.i(TAG, "Token saved successfully, expires: ${Date(expirationTime)}")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving token: ${e.message}", e)
            }
        }
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        withContext(Dispatchers.IO) {
            try {
                // Calculate expiration time (24 hours from now)
                val expirationTime = System.currentTimeMillis() + DEFAULT_TOKEN_EXPIRATION

                secureStorageManager.safeStringWrite(sharedPreferences, KEY_ACCESS_TOKEN, accessToken)
                secureStorageManager.safeStringWrite(sharedPreferences, KEY_REFRESH_TOKEN, refreshToken)
                secureStorageManager.safeLongWrite(sharedPreferences, KEY_TOKEN_EXPIRATION, expirationTime)
                secureStorageManager.safeBooleanWrite(sharedPreferences, KEY_REFRESH_USED, false) // Reset refresh usage flag

                Log.i(TAG, "Tokens saved successfully, expires: ${Date(expirationTime)}")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving tokens: ${e.message}", e)
            }
        }
    }

    override suspend fun getToken(): String? {
        return withContext(Dispatchers.IO) {
            try {
                secureStorageManager.safeStringRead(sharedPreferences, KEY_ACCESS_TOKEN)
            } catch (e: Exception) {
                Log.e(TAG, "Error retrieving access token: ${e.message}", e)
                null
            }
        }
    }

    override suspend fun getRefreshToken(): String? {
        return withContext(Dispatchers.IO) {
            try {
                secureStorageManager.safeStringRead(sharedPreferences, KEY_REFRESH_TOKEN)
            } catch (e: Exception) {
                Log.e(TAG, "Error retrieving refresh token: ${e.message}", e)
                null
            }
        }
    }

    override suspend fun updateAccessToken(newAccessToken: String) {
        withContext(Dispatchers.IO) {
            try {
                // Calculate new expiration time (24 hours from now)
                val expirationTime = System.currentTimeMillis() + DEFAULT_TOKEN_EXPIRATION

                secureStorageManager.safeStringWrite(sharedPreferences, KEY_ACCESS_TOKEN, newAccessToken)
                secureStorageManager.safeLongWrite(sharedPreferences, KEY_TOKEN_EXPIRATION, expirationTime)

                Log.i(TAG, "Access token updated successfully, expires: ${Date(expirationTime)}")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating access token: ${e.message}", e)
            }
        }
    }

    override suspend fun clearToken() {
        withContext(Dispatchers.IO) {
            try {
                secureStorageManager.safeClearAll(sharedPreferences)
                Log.i(TAG, "Tokens cleared")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing tokens: ${e.message}", e)

                // If clearing fails, try with a more aggressive approach
                try {
                    context.getSharedPreferences("encrypted_token_preferences", Context.MODE_PRIVATE)
                        .edit().clear().apply()
                    Log.i(TAG, "Forcibly cleared token preferences")
                } catch (e2: Exception) {
                    Log.e(TAG, "Failed to forcibly clear token preferences: ${e2.message}", e2)
                }
            }
        }
    }

    override fun isLoggedIn(): Boolean {
        return try {
            val isLoggedIn = sharedPreferences.contains(KEY_ACCESS_TOKEN)
            Log.i(TAG, "User login status: ${if (isLoggedIn) "LOGGED IN" else "NOT LOGGED IN"}")
            isLoggedIn
        } catch (e: Exception) {
            Log.e(TAG, "Error checking login status: ${e.message}", e)
            false
        }
    }

    override suspend fun isTokenExpired(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val expirationTime = secureStorageManager.safeLongRead(
                    sharedPreferences,
                    KEY_TOKEN_EXPIRATION,
                    0L
                )
                val isExpired = System.currentTimeMillis() > expirationTime

                if (isExpired) {
                    Log.i(TAG, "Token is expired")
                }

                isExpired
            } catch (e: Exception) {
                Log.e(TAG, "Error checking token expiration: ${e.message}", e)
                true // Assume expired if there's an error
            }
        }
    }

    override suspend fun isTokenExpiringSoon(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val expirationTime = secureStorageManager.safeLongRead(
                    sharedPreferences,
                    KEY_TOKEN_EXPIRATION,
                    0L
                )
                val expiringSoon = System.currentTimeMillis() > expirationTime - REFRESH_BUFFER_TIME

                if (expiringSoon) {
                    Log.i(TAG, "Token is expiring soon")
                }

                expiringSoon
            } catch (e: Exception) {
                Log.e(TAG, "Error checking if token is expiring soon: ${e.message}", e)
                true // Assume expiring soon if there's an error
            }
        }
    }

    /**
     * Utility function to check if the refresh token has been used
     */
    suspend fun hasUsedRefresh(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                secureStorageManager.safeBooleanRead(
                    sharedPreferences,
                    KEY_REFRESH_USED,
                    false
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error checking refresh token usage: ${e.message}", e)
                false
            }
        }
    }

    /**
     * Utility function to mark the refresh token as used
     */
    suspend fun markRefreshUsed() {
        withContext(Dispatchers.IO) {
            try {
                secureStorageManager.safeBooleanWrite(
                    sharedPreferences,
                    KEY_REFRESH_USED,
                    true
                )
                Log.i(TAG, "Refresh token marked as used")
            } catch (e: Exception) {
                Log.e(TAG, "Error marking refresh token as used: ${e.message}", e)
            }
        }
    }
}