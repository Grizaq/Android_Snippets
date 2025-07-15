package com.chirilglance.androidglancedna

import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.chirilglance.androidglancedna.data.auth.AuthEventBus
import com.chirilglance.androidglancedna.domain.auth.TokenManager
import com.chirilglance.androidglancedna.presentation.MainActivity
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    companion object {
        const val TAG = "GlanceDNA"
    }

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var authEventBus: AuthEventBus

    // Application scope for background operations
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {

        try {
            FirebaseApp.initializeApp(this)
            Log.i(TAG, "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase: ${e.message}", e)
        }

        super.onCreate()
        // Set up authentication failure listener
        setupAuthFailureListener()
    }

    private fun initializeFirebase() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.i(TAG, "Firebase initialized successfully")
            } else {
                Log.i(TAG, "Firebase already initialized")
            }
        } catch (e: Exception) {
            // Log error but don't crash the app if Firebase initialization fails
            Log.e(TAG, "Error initializing Firebase: ${e.message}", e)
        }
    }

    private fun setupAuthFailureListener() {
        applicationScope.launch {
            authEventBus.authEvents.collectLatest { event ->
                when (event) {
                    is AuthEventBus.AuthEvent.AuthFailure -> handleAuthFailure()
                    is AuthEventBus.AuthEvent.Login -> handleLogin(event.token)
                    is AuthEventBus.AuthEvent.Logout -> handleLogout()
                    is AuthEventBus.AuthEvent.TokenRefresh -> handleTokenRefresh(event.newToken)
                }
            }
        }
    }

    private fun handleAuthFailure() {
        applicationScope.launch(Dispatchers.Main) {
            // Clear token
            tokenManager.clearToken()

            // Show toast message
            Toast.makeText(
                this@MainApplication,
                "Your session has expired. Please log in again.",
                Toast.LENGTH_LONG
            ).show()

            // Launch the MainActivity with a flag to show login
            val intent = Intent(this@MainApplication, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("SESSION_EXPIRED", true)
            }
            startActivity(intent)
        }
    }

    private fun handleLogin(token: String) {
        // Additional login handling could be added here
    }

    private fun handleLogout() {
        // Additional logout handling could be added here
    }

    private fun handleTokenRefresh(newToken: String) {
        // Additional token refresh handling could be added here
    }
}