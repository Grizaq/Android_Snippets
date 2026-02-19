package com.chirilglance.androidglancedna.data.auth.repository

import android.util.Log
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.data.auth.config.AuthConfig
import com.chirilglance.androidglancedna.data.auth.remote.EmailAuthResult
import com.chirilglance.androidglancedna.data.auth.remote.FirebaseDataSource
import com.chirilglance.androidglancedna.data.auth.remote.FirebaseEmailAuthDataSource
import com.chirilglance.androidglancedna.domain.auth.TokenManager
import com.chirilglance.androidglancedna.domain.auth.model.User
import com.chirilglance.androidglancedna.domain.auth.repository.EmailAuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of EmailAuthRepository using Firebase.
 */
@Singleton
class EmailAuthRepositoryImpl @Inject constructor(
    private val emailAuthDataSource: FirebaseEmailAuthDataSource,
    private val firebaseDataSource: FirebaseDataSource,
    private val tokenManager: TokenManager
) : EmailAuthRepository {

    companion object {
        private const val TAG = "Auth"
    }

    // Repository-scoped coroutine scope for sign-out operations
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override suspend fun registerWithEmail(
        email: String,
        password: String
    ): Flow<UiState<User>> = flow {
        emit(UiState.Loading)

        when (val result = emailAuthDataSource.registerWithEmail(email, password)) {
            is EmailAuthResult.Success -> {
                Log.d(TAG, "Email registration successful for user: ${result.userId}")

                // Get Firebase token
                val token = firebaseDataSource.getIdToken(forceRefresh = false)

                if (token != null) {
                    // Save token
                    tokenManager.saveToken(token)

                    // Create user object
                    val user = User(
                        id = result.userId,
                        email = email,
                        role = "user"
                    )

                    // Save to Firestore
                    if (AuthConfig.STORE_IN_FIRESTORE) {
                        try {
                            firebaseDataSource.saveUserToFirestore(user, isNewUser = true)
                            Log.d(TAG, "User saved to Firestore: ${result.userId}")
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to save user to Firestore: ${e.message}", e)
                        }
                    }

                    emit(UiState.Success(user))
                } else {
                    Log.e(TAG, "Failed to get Firebase token after registration")
                    emit(UiState.Error("Failed to complete registration"))
                }
            }

            is EmailAuthResult.Error -> {
                Log.e(TAG, "Email registration failed: ${result.message}")
                emit(UiState.Error(result.message))
            }
        }
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): Flow<UiState<User>> = flow {
        emit(UiState.Loading)

        when (val result = emailAuthDataSource.signInWithEmail(email, password)) {
            is EmailAuthResult.Success -> {
                Log.d(TAG, "Email sign-in successful for user: ${result.userId}")

                // Get Firebase token
                val token = firebaseDataSource.getIdToken(forceRefresh = false)

                if (token != null) {
                    // Save token
                    tokenManager.saveToken(token)

                    // Get user from Firestore or create from Firebase Auth
                    var user = if (AuthConfig.STORE_IN_FIRESTORE) {
                        firebaseDataSource.getUserFromFirestore(result.userId)
                    } else null

                    if (user == null) {
                        // User not in Firestore, create from Firebase Auth
                        user = User(
                            id = result.userId,
                            email = email,
                            role = "user"
                        )
                    }

                    // Update last login
                    if (AuthConfig.STORE_IN_FIRESTORE) {
                        try {
                            firebaseDataSource.saveUserToFirestore(user, isNewUser = false)
                            Log.d(TAG, "Updated last login for user: ${result.userId}")
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to update last login: ${e.message}", e)
                        }
                    }

                    emit(UiState.Success(user))
                } else {
                    Log.e(TAG, "Failed to get Firebase token after sign-in")
                    emit(UiState.Error("Failed to complete sign-in"))
                }
            }

            is EmailAuthResult.Error -> {
                Log.e(TAG, "Email sign-in failed: ${result.message}")
                emit(UiState.Error(result.message))
            }
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Flow<UiState<Boolean>> = flow {
        emit(UiState.Loading)

        val success = emailAuthDataSource.sendPasswordResetEmail(email)

        if (success) {
            Log.d(TAG, "Password reset email sent to: $email")
            emit(UiState.Success(true))
        } else {
            Log.e(TAG, "Failed to send password reset email to: $email")
            emit(UiState.Error("Failed to send password reset email. Please check the email address."))
        }
    }

    override fun signOut() {
        repositoryScope.launch {
            try {
                emailAuthDataSource.signOut()
                tokenManager.clearToken()
                Log.d(TAG, "User signed out successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Sign out error: ${e.message}", e)
            }
        }
    }
}