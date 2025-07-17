package com.chirilglance.androidglancedna.core.data.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A secure storage manager that uses EncryptedSharedPreferences with corruption recovery.
 *
 * This class provides a foundation for secure data storage with automatic handling of
 * potential SharedPreferences corruption issues.
 */
class SecureStorageManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "SecureStorageManager"
    }

    // Create or get the master key for encryption
    private val masterKeyAlias by lazy {
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    }

    /**
     * Creates encrypted preferences with error handling and fallback mechanism.
     *
     * @param preferenceName The name of the preferences file
     * @return An encrypted SharedPreferences instance or a fallback if encryption fails
     */
    fun createEncryptedPreferences(preferenceName: String): SharedPreferences {
        return try {
            EncryptedSharedPreferences.create(
                preferenceName,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error creating encrypted preferences: ${e.message}", e)

            // Handle corruption - clear the corrupted file
            try {
                context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
                    .edit().clear().apply()
                Log.i(TAG, "Cleared corrupted preferences for $preferenceName")

                // Try again after clearing
                EncryptedSharedPreferences.create(
                    preferenceName,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e2: Exception) {
                // If still failing, fall back to regular preferences as a last resort
                Log.e(TAG, "Still unable to create encrypted preferences: ${e2.message}", e2)
                Log.w(TAG, "Falling back to regular SharedPreferences for $preferenceName")
                context.getSharedPreferences("fallback_$preferenceName", Context.MODE_PRIVATE)
            }
        }
    }

    /**
     * Safely writes a string value to the given SharedPreferences.
     *
     * @param preferences The SharedPreferences to write to
     * @param key The key for the value
     * @param value The string value to write
     * @return Whether the operation was successful
     */
    suspend fun safeStringWrite(
        preferences: SharedPreferences,
        key: String,
        value: String?
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            if (value == null) {
                preferences.edit().remove(key).apply()
            } else {
                preferences.edit().putString(key, value).apply()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error writing $key to preferences: ${e.message}", e)
            false
        }
    }

    /**
     * Safely reads a string value from the given SharedPreferences.
     *
     * @param preferences The SharedPreferences to read from
     * @param key The key for the value to read
     * @param defaultValue The default value to return if the key is not found
     * @return The string value or null if an error occurred
     */
    suspend fun safeStringRead(
        preferences: SharedPreferences,
        key: String,
        defaultValue: String? = null
    ): String? = withContext(Dispatchers.IO) {
        try {
            preferences.getString(key, defaultValue)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading $key from preferences: ${e.message}", e)
            defaultValue
        }
    }

    /**
     * Safely writes a boolean value to the given SharedPreferences.
     *
     * @param preferences The SharedPreferences to write to
     * @param key The key for the value
     * @param value The boolean value to write
     * @return Whether the operation was successful
     */
    suspend fun safeBooleanWrite(
        preferences: SharedPreferences,
        key: String,
        value: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            preferences.edit().putBoolean(key, value).apply()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error writing boolean $key to preferences: ${e.message}", e)
            false
        }
    }

    /**
     * Safely reads a boolean value from the given SharedPreferences.
     *
     * @param preferences The SharedPreferences to read from
     * @param key The key for the value to read
     * @param defaultValue The default value to return if the key is not found
     * @return The boolean value or defaultValue if an error occurred
     */
    suspend fun safeBooleanRead(
        preferences: SharedPreferences,
        key: String,
        defaultValue: Boolean = false
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            preferences.getBoolean(key, defaultValue)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading boolean $key from preferences: ${e.message}", e)
            defaultValue
        }
    }

    /**
     * Safely writes a long value to the given SharedPreferences.
     *
     * @param preferences The SharedPreferences to write to
     * @param key The key for the value
     * @param value The long value to write
     * @return Whether the operation was successful
     */
    suspend fun safeLongWrite(
        preferences: SharedPreferences,
        key: String,
        value: Long
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            preferences.edit().putLong(key, value).apply()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error writing long $key to preferences: ${e.message}", e)
            false
        }
    }

    /**
     * Safely reads a long value from the given SharedPreferences.
     *
     * @param preferences The SharedPreferences to read from
     * @param key The key for the value to read
     * @param defaultValue The default value to return if the key is not found
     * @return The long value or defaultValue if an error occurred
     */
    suspend fun safeLongRead(
        preferences: SharedPreferences,
        key: String,
        defaultValue: Long = 0L
    ): Long = withContext(Dispatchers.IO) {
        try {
            preferences.getLong(key, defaultValue)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading long $key from preferences: ${e.message}", e)
            defaultValue
        }
    }

    /**
     * Safely removes a key from the given SharedPreferences.
     *
     * @param preferences The SharedPreferences to modify
     * @param key The key to remove
     * @return Whether the operation was successful
     */
    suspend fun safeRemoveKey(
        preferences: SharedPreferences,
        key: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            preferences.edit().remove(key).apply()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error removing $key from preferences: ${e.message}", e)
            false
        }
    }

    /**
     * Safely clears all values from the given SharedPreferences.
     *
     * @param preferences The SharedPreferences to clear
     * @return Whether the operation was successful
     */
    suspend fun safeClearAll(preferences: SharedPreferences): Boolean = withContext(Dispatchers.IO) {
        try {
            preferences.edit().clear().apply()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing preferences: ${e.message}", e)
            false
        }
    }
}