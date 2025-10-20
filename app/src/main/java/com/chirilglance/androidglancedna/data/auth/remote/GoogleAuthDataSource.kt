package com.chirilglance.androidglancedna.data.auth.remote

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for Google Sign-In operations.
 *
 * This class uses the traditional GoogleSignInClient which works for both new and returning users.
 * One Tap Sign-In only works if users have previously signed into your app.
 */
@Singleton
class GoogleAuthDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "GoogleAuthDataSource"
    }

    // Traditional Google Sign-In client (works for first-time users)
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(com.chirilglance.androidglancedna.R.string.default_web_client_id))
            .requestEmail()
            .build()

        GoogleSignIn.getClient(context, gso)
    }

    /**
     * Get the sign-in intent for traditional Google Sign-In.
     * This works for both new and returning users.
     *
     * @return Intent to launch Google Sign-In activity
     */
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    /**
     * Get the Google ID token from the sign-in result.
     *
     * @param data The result intent from the Google Sign-In activity
     * @return The Google ID token, or null if extraction fails
     */
    suspend fun getSignInResultFromIntent(data: Intent?): String? {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            Log.d(TAG, "Sign-in successful for: ${account?.email}")
            account?.idToken
        } catch (e: ApiException) {
            Log.e(TAG, "Sign-in failed: ${e.statusCode} - ${e.message}")
            null
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error: ${e.message}", e)
            null
        }
    }

    /**
     * Sign out from Google.
     * This clears the saved account selection.
     */
    suspend fun signOut() {
        try {
            googleSignInClient.signOut().await()
            Log.d(TAG, "Sign out successful")
        } catch (e: Exception) {
            Log.e(TAG, "Sign out error: ${e.message}", e)
        }
    }

    /**
     * Revoke access (complete disconnect).
     * Use this for "Delete Account" functionality.
     */
    suspend fun revokeAccess() {
        try {
            googleSignInClient.revokeAccess().await()
            Log.d(TAG, "Access revoked")
        } catch (e: Exception) {
            Log.e(TAG, "Revoke access error: ${e.message}", e)
        }
    }
}