package com.chirilglance.androidglancedna.presentation.examples.auth.google

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chirilglance.androidglancedna.core.ui.components.SnackbarManager
import com.chirilglance.androidglancedna.domain.auth.model.GoogleAuthResult
import com.chirilglance.androidglancedna.domain.auth.model.User
import com.chirilglance.androidglancedna.domain.auth.repository.GoogleAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Google Sign-In operations.
 *
 * Handles:
 * - Initiating Google Sign-In flow
 * - Processing sign-in results
 * - Managing authentication state
 * - Error handling and user feedback
 */
@HiltViewModel
class GoogleSignInViewModel @Inject constructor(
    private val googleAuthRepository: GoogleAuthRepository
) : ViewModel() {

    // UI state for sign-in flow
    private val _signInState = MutableStateFlow<GoogleSignInState>(GoogleSignInState.Idle)
    val signInState: StateFlow<GoogleSignInState> = _signInState.asStateFlow()

    // Current authenticated user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        // Check if user is already signed in
        checkSignInStatus()
    }

    /**
     * Check if user is currently signed in and load user data
     */
    private fun checkSignInStatus() {
        viewModelScope.launch {
            if (googleAuthRepository.isSignedIn()) {
                val user = googleAuthRepository.getCurrentUser()
                _currentUser.update { user }
            }
        }
    }

    /**
     * Get the Google Sign-In intent.
     *
     * This returns the intent immediately - no suspend needed.
     * Launch this intent to show the Google account picker.
     */
    fun getSignInIntent(): Intent {
        android.util.Log.d("GoogleSignInViewModel", "Getting sign-in intent...")
        _signInState.update { GoogleSignInState.Loading }
        val intent = googleAuthRepository.getSignInIntent()
        android.util.Log.d("GoogleSignInViewModel", "Sign-in intent created successfully")
        return intent
    }

    /**
     * Handle the result from Google Sign-In activity.
     *
     * Call this from your Activity's onActivityResult or Activity Result API callback.
     *
     * @param intent The result intent from the sign-in activity
     * @param onSuccess Callback when sign-in succeeds
     */
    fun handleSignInResult(intent: Intent?, onSuccess: (User) -> Unit) {
        if (intent == null) {
            _signInState.update { GoogleSignInState.Idle }
            SnackbarManager.showInfo("Sign-in cancelled")
            return
        }

        viewModelScope.launch {
            _signInState.update { GoogleSignInState.Loading }

            when (val result = googleAuthRepository.signInWithGoogle(intent)) {
                is GoogleAuthResult.Success -> {
                    _currentUser.update { result.user }
                    _signInState.update { GoogleSignInState.Success(result.user) }

                    SnackbarManager.showSuccess(
                        message = "Welcome, ${result.user.getDisplayName()}!",
                        duration = com.chirilglance.androidglancedna.core.ui.components.SnackbarDuration.SHORT
                    )

                    onSuccess(result.user)
                }

                is GoogleAuthResult.Error -> {
                    _signInState.update { GoogleSignInState.Error(result.message) }
                    SnackbarManager.showError(result.message)
                }

                is GoogleAuthResult.Cancelled -> {
                    _signInState.update { GoogleSignInState.Idle }
                    SnackbarManager.showInfo("Sign-in cancelled")
                }

                is GoogleAuthResult.NetworkError -> {
                    _signInState.update { GoogleSignInState.Error(result.message) }
                    SnackbarManager.showError(result.message)
                }
            }
        }
    }

    /**
     * Sign out the current user
     */
    fun signOut(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                googleAuthRepository.signOut()
                _currentUser.update { null }
                _signInState.update { GoogleSignInState.Idle }

                SnackbarManager.showSuccess("Signed out successfully")
                onComplete()
            } catch (e: Exception) {
                SnackbarManager.showError("Sign out failed: ${e.message}")
            }
        }
    }

    /**
     * Reset state to idle (e.g., after handling the result)
     */
    fun resetState() {
        _signInState.update { GoogleSignInState.Idle }
    }
}

/**
 * UI state for Google Sign-In flow
 */
sealed class GoogleSignInState {
    /** Initial state, no action taken */
    object Idle : GoogleSignInState()

    /** Loading/processing sign-in */
    object Loading : GoogleSignInState()

    /** Sign-in completed successfully */
    data class Success(val user: User) : GoogleSignInState()

    /** Sign-in failed with error */
    data class Error(val message: String) : GoogleSignInState()
}