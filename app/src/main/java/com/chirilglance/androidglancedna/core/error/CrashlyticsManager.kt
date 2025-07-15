package com.chirilglance.androidglancedna.core.error

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.chirilglance.androidglancedna.domain.models.profile.UserProfile
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashlyticsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "CrashlyticsManager"
    }

    private val crashlytics by lazy {
        try {
            // Try to get the instance directly
            FirebaseCrashlytics.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get Crashlytics instance: ${e.message}", e)

            // Try to initialize Firebase again if it failed the first time
            try {
                FirebaseApp.initializeApp(context)
                FirebaseCrashlytics.getInstance()
            } catch (e2: Exception) {
                Log.e(
                    TAG,
                    "Still failed to get Crashlytics after re-initializing: ${e2.message}",
                    e2
                )
                null
            }
        }
    }

    init {
        setupDefaultKeys()
    }

    private fun setupDefaultKeys() {
        try {
            crashlytics?.apply {
                // Device information
                setCustomKey("device_model", Build.MODEL)
                setCustomKey("device_manufacturer", Build.MANUFACTURER)
                setCustomKey("android_version", Build.VERSION.RELEASE)
                setCustomKey("android_sdk", Build.VERSION.SDK_INT.toString())

                // App information
                try {
                    val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        context.packageManager.getPackageInfo(
                            context.packageName, PackageManager.PackageInfoFlags.of(0)
                        )
                    } else {
                        @Suppress("DEPRECATION") context.packageManager.getPackageInfo(
                            context.packageName,
                            0
                        )
                    }

                    val versionName = packageInfo.versionName ?: "unknown"
                    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        packageInfo.longVersionCode.toString()
                    } else {
                        @Suppress("DEPRECATION") packageInfo.versionCode.toString()
                    }

                    setCustomKey("app_version", versionName)
                    setCustomKey("app_build", versionCode)
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting package info: ${e.message}", e)
                    setCustomKey("app_version", "unknown")
                    setCustomKey("app_build", "0")
                }

                // Session information
                setCustomKey("session_start_time", System.currentTimeMillis())

                log("Crashlytics initialized with default keys")

                // Set collection enabled (you can add a toggle in settings later)
                isCrashlyticsCollectionEnabled = true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Crashlytics: ${e.message}", e)
        }
    }

    fun setUserInfo(userId: String, userProfile: UserProfile? = null) {
        try {
            crashlytics?.apply {
                setUserId(userId)

                userProfile?.let {
                    setCustomKey("user_role", it.roleType.name)
                    setCustomKey("user_display_name", it.displayName)
                    setCustomKey("user_verified", it.isVerified)

                    if (it.hasTeam()) {
                        setCustomKey("user_team_id", it.teamId ?: "")
                        setCustomKey("user_team_name", it.teamName ?: "")
                    }

                    log("Set user info for: ${it.displayName} (${it.roleType})")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting user info: ${e.message}", e)
        }
    }

    fun clearUserInfo() {
        try {
            crashlytics?.apply {
                setUserId("")
                setCustomKey("user_role", "")
                setCustomKey("user_display_name", "")
                setCustomKey("user_verified", false)
                setCustomKey("user_team_id", "")
                setCustomKey("user_team_name", "")
                log("User info cleared")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing user info: ${e.message}", e)
        }
    }

    fun log(message: String) {
        try {
            crashlytics?.log(message)
        } catch (e: Exception) {
            Log.e(TAG, "Error logging to Crashlytics: ${e.message}", e)
        }
    }

    fun recordException(throwable: Throwable, customMessage: String? = null) {
        try {
            crashlytics?.apply {
                customMessage?.let { log(it) }
                recordException(throwable)
            }
            Log.e(TAG, "Recorded exception: ${throwable.message}", throwable)
        } catch (e: Exception) {
            Log.e(TAG, "Error recording exception: ${e.message}", e)
        }
    }

    fun setCustomKey(key: String, value: String) {
        try {
            crashlytics?.setCustomKey(key, value)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting custom key: ${e.message}", e)
        }
    }

    fun setCustomKey(key: String, value: Boolean) {
        try {
            crashlytics?.setCustomKey(key, value)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting custom key: ${e.message}", e)
        }
    }

    fun setCustomKey(key: String, value: Int) {
        try {
            crashlytics?.setCustomKey(key, value)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting custom key: ${e.message}", e)
        }
    }

    fun setCustomKey(key: String, value: Long) {
        try {
            crashlytics?.setCustomKey(key, value)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting custom key: ${e.message}", e)
        }
    }

    fun setCustomKey(key: String, value: Float) {
        try {
            crashlytics?.setCustomKey(key, value)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting custom key: ${e.message}", e)
        }
    }

    fun setCustomKey(key: String, value: Double) {
        try {
            crashlytics?.setCustomKey(key, value)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting custom key: ${e.message}", e)
        }
    }
}