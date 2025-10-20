package com.chirilglance.androidglancedna.data.auth.repository

import android.content.Intent
import android.util.Log
import com.chirilglance.androidglancedna.data.auth.config.AuthConfig
import com.chirilglance.androidglancedna.data.auth.remote.BackendAuthApi
import com.chirilglance.androidglancedna.data.auth.remote.FirebaseDataSource
import com.chirilglance.androidglancedna.data.auth.remote.GoogleAuthDataSource
import com.chirilglance.androidglancedna.domain.auth.TokenManager
import com.chirilglance.androidglancedna.domain.auth.model.AuthMode
import com.chirilglance.androidglancedna.domain.auth.model.GoogleAuthResult
import com.chirilglance.androidglancedna.domain.auth.model.User
import com.chirilglance.androidglancedna.domain.auth.repository.GoogleAuthRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of GoogleAuthRepository that supports multiple authentication modes.
 *
 * This repository orchestrates the authentication flow by coordinating between:
 * - GoogleAuthDataSource: Google Sign-In operations
 * - FirebaseDataSource: Firebase authentication and Firestore operations
 * - BackendAuthApi: Optional backend integration
 * - TokenManager: Secure token storage
 *
 * The behavior changes based on AuthConfig.authMode:
 * - FIREBASE_ONLY: Google → Firebase → Store Firebase token
 * - FIREBASE_BACKEND: Google → Firebase → Backend exchange → Store backend token
 * - BACKEND_ONLY: Not applicable for Google sign-in
 */
@Singleton
class GoogleAuthRepositoryImpl @Inject constructor(
    private val googleAuthDataSource: GoogleAuthDataSource,
    private val firebaseDataSource: FirebaseDataSource,
    private val backendAuthApi: BackendAuthApi,
    private val tokenManager: TokenManager
) : GoogleAuthRepository {

    companion object {
        private const val TAG = "GoogleAuthRepository"
    }

    override fun getSignInIntent(): Intent {
        return googleAuthDataSource.getSignInIntent()
    }

    override suspend fun signInWithGoogle(intent: Intent): GoogleAuthResult {
        return try {
            // Step 1: Get Google ID token from sign-in result
            val googleIdToken = googleAuthDataSource.getSignInResultFromIntent(intent)

            if (googleIdToken == null) {
                return GoogleAuthResult.Cancelled
            }

            // Step 2: Handle authentication based on mode
            when (AuthConfig.authMode) {
                AuthMode.FIREBASE_ONLY -> handleFirebaseOnlyAuth(googleIdToken)
                AuthMode.FIREBASE_BACKEND -> handleFirebaseBackendAuth(googleIdToken)
                AuthMode.BACKEND_ONLY -> {
                    // Backend-only mode doesn't use Google OAuth
                    Log.w(TAG, "BACKEND_ONLY mode doesn't support Google sign-in")
                    GoogleAuthResult.Error(
                        exception = Exception("Google sign-in not supported in BACKEND_ONLY mode"),
                        message = "This authentication method is not available"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sign in failed: ${e.message}", e)
            GoogleAuthResult.Error(
                exception = e,
                message = "Sign in failed. Please try again."
            )
        }
    }

    /**
     * Handle Firebase-only authentication flow.
     *
     * Flow:
     * 1. Sign in to Firebase with Google token
     * 2. Get Firebase ID token
     * 3. Store Firebase token in TokenManager
     * 4. Optionally save user to Firestore
     */
    private suspend fun handleFirebaseOnlyAuth(googleIdToken: String): GoogleAuthResult {
        return try {
            // Sign in to Firebase
            val (user, firebaseToken) = firebaseDataSource.signInWithGoogle(googleIdToken)
                ?: return GoogleAuthResult.Error(
                    exception = Exception("Firebase sign-in failed"),
                    message = "Failed to authenticate with Firebase"
                )

            // Store Firebase token
            tokenManager.saveToken(firebaseToken)

            Log.i(TAG, "Firebase-only sign in successful for user: ${user.id}")
            GoogleAuthResult.Success(
                user = user,
                token = firebaseToken,
                isNewUser = false // Firebase handles this internally
            )
        } catch (e: Exception) {
            Log.e(TAG, "Firebase-only auth failed: ${e.message}", e)
            GoogleAuthResult.Error(
                exception = e,
                message = "Authentication failed. Please try again."
            )
        }
    }

    /**
     * Handle Firebase + Backend hybrid authentication flow.
     *
     * Flow:
     * 1. Sign in to Firebase with Google token
     * 2. Get Firebase ID token
     * 3. Exchange Firebase token with backend
     * 4. Store backend token in TokenManager
     * 5. Optionally save user to Firestore
     */
    private suspend fun handleFirebaseBackendAuth(googleIdToken: String): GoogleAuthResult {
        return try {
            // Step 1: Sign in to Firebase first
            val (firebaseUser, firebaseToken) = firebaseDataSource.signInWithGoogle(googleIdToken)
                ?: return GoogleAuthResult.Error(
                    exception = Exception("Firebase sign-in failed"),
                    message = "Failed to authenticate with Firebase"
                )

            // Step 2: Exchange Firebase token with backend
            val (backendUser, backendToken) = backendAuthApi.exchangeGoogleToken(firebaseToken)
                ?: return GoogleAuthResult.Error(
                    exception = Exception("Backend token exchange failed"),
                    message = "Failed to authenticate with backend"
                )

            // Step 3: Store backend token (not Firebase token)
            tokenManager.saveToken(backendToken)

            Log.i(TAG, "Firebase-backend sign in successful for user: ${backendUser.id}")
            GoogleAuthResult.Success(
                user = backendUser,
                token = backendToken,
                isNewUser = false
            )
        } catch (e: Exception) {
            Log.e(TAG, "Firebase-backend auth failed: ${e.message}", e)
            GoogleAuthResult.Error(
                exception = e,
                message = "Authentication failed. Please try again."
            )
        }
    }

    override suspend fun getCurrentUser(): User? {
        return try {
            when (AuthConfig.authMode) {
                AuthMode.FIREBASE_ONLY, AuthMode.FIREBASE_BACKEND -> {
                    // Get user from Firebase or Firestore
                    val firebaseUser = firebaseDataSource.getCurrentFirebaseUser()
                    if (firebaseUser != null && AuthConfig.STORE_IN_FIRESTORE) {
                        // Try to get full user data from Firestore
                        firebaseDataSource.getUserFromFirestore(firebaseUser.id) ?: firebaseUser
                    } else {
                        firebaseUser
                    }
                }
                AuthMode.BACKEND_ONLY -> {
                    // Backend-only mode would need a separate implementation
                    // For now, return null
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get current user: ${e.message}", e)
            null
        }
    }

    override suspend fun signOut() {
        try {
            // Clear token from storage
            tokenManager.clearToken()

            // Sign out from Firebase
            when (AuthConfig.authMode) {
                AuthMode.FIREBASE_ONLY, AuthMode.FIREBASE_BACKEND -> {
                    firebaseDataSource.signOut()
                    googleAuthDataSource.signOut()
                }
                AuthMode.BACKEND_ONLY -> {
                    // Backend-only sign out would be handled differently
                }
            }

            Log.i(TAG, "Sign out successful")
        } catch (e: Exception) {
            Log.e(TAG, "Sign out failed: ${e.message}", e)
        }
    }

    override fun isSignedIn(): Boolean {
        return try {
            // Check if we have a valid token
            val hasToken = tokenManager.isLoggedIn()

            // For Firebase modes, also check Firebase auth state
            val isFirebaseSignedIn = when (AuthConfig.authMode) {
                AuthMode.FIREBASE_ONLY, AuthMode.FIREBASE_BACKEND -> {
                    firebaseDataSource.isSignedIn()
                }
                AuthMode.BACKEND_ONLY -> true // Backend mode doesn't use Firebase
            }

            hasToken && isFirebaseSignedIn
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check sign in status: ${e.message}", e)
            false
        }
    }
}