package com.chirilglance.androidglancedna.domain.auth.repository

import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.domain.auth.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for email/password authentication.
 */
interface EmailAuthRepository {

    /**
     * Register a new user with email and password.
     *
     * @param email User's email address
     * @param password User's password (min 6 characters)
     * @return Flow emitting UI states for registration
     */
    suspend fun registerWithEmail(
        email: String,
        password: String
    ): Flow<UiState<User>>

    /**
     * Sign in existing user with email and password.
     *
     * @param email User's email address
     * @param password User's password
     * @return Flow emitting UI states for sign-in
     */
    suspend fun signInWithEmail(
        email: String,
        password: String
    ): Flow<UiState<User>>

    /**
     * Send password reset email to user.
     *
     * @param email User's email address
     * @return Flow emitting UI states for password reset
     */
    suspend fun sendPasswordResetEmail(email: String): Flow<UiState<Boolean>>

    /**
     * Sign out current user.
     */
    fun signOut()
}