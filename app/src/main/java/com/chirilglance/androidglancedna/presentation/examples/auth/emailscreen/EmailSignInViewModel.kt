package com.chirilglance.androidglancedna.presentation.examples.auth.emailscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.SnackbarManager
import com.chirilglance.androidglancedna.core.validation.ValidationUtils
import com.chirilglance.androidglancedna.domain.auth.model.User
import com.chirilglance.androidglancedna.domain.auth.repository.EmailAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailSignInViewModel @Inject constructor(
    private val emailAuthRepository: EmailAuthRepository
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow<UiState<User>>(UiState.Empty)
    val uiState: StateFlow<UiState<User>> = _uiState.asStateFlow()

    // Form fields
    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    // Track if user is in register mode or sign-in mode
    var isRegisterMode by mutableStateOf(false)
        private set

    // Update methods
    fun updateEmail(value: String) {
        email = value
        if (_uiState.value is UiState.Error) {
            _uiState.update { UiState.Empty }
        }
    }

    fun updatePassword(value: String) {
        password = value
        if (_uiState.value is UiState.Error) {
            _uiState.update { UiState.Empty }
        }
    }

    fun toggleMode() {
        isRegisterMode = !isRegisterMode
        _uiState.update { UiState.Empty }
    }

    // Validation using your existing ValidationUtils
    fun validateEmail() = ValidationUtils.validateEmail(email)

    fun validatePassword() = ValidationUtils.validatePassword(password)

    fun canSubmit(): Boolean {
        return validateEmail().isValid && validatePassword().isValid
    }

    /**
     * Sign in or register with email/password
     */
    fun submitEmailAuth(onSuccess: (User) -> Unit) {
        if (!canSubmit()) {
            val emailValidation = validateEmail()
            val passwordValidation = validatePassword()

            val errorMessage = when {
                !emailValidation.isValid -> emailValidation.errorMessage
                !passwordValidation.isValid -> passwordValidation.errorMessage
                else -> "Please check your input"
            }

            _uiState.update { UiState.Error(errorMessage ?: "Invalid input") }
            SnackbarManager.showError(errorMessage ?: "Invalid input")
            return
        }

        viewModelScope.launch {
            val flow = if (isRegisterMode) {
                emailAuthRepository.registerWithEmail(email, password)
            } else {
                emailAuthRepository.signInWithEmail(email, password)
            }

            flow.collect { state ->
                _uiState.value = state

                when (state) {
                    is UiState.Success -> {
                        val message = if (isRegisterMode) {
                            "Account created successfully!"
                        } else {
                            "Welcome back!"
                        }
                        SnackbarManager.showSuccess(message)
                        onSuccess(state.data)
                    }

                    is UiState.Error -> {
                        SnackbarManager.showError(state.message)
                    }

                    else -> { /* Loading or Empty */ }
                }
            }
        }
    }

    /**
     * Send password reset email
     */
    fun sendPasswordReset() {
        val emailValidation = validateEmail()
        if (!emailValidation.isValid) {
            SnackbarManager.showError(emailValidation.errorMessage ?: "Invalid email")
            return
        }

        viewModelScope.launch {
            emailAuthRepository.sendPasswordResetEmail(email).collect { state ->
                when (state) {
                    is UiState.Success -> {
                        SnackbarManager.showSuccess("Password reset email sent! Check your inbox.")
                    }

                    is UiState.Error -> {
                        SnackbarManager.showError(state.message)
                    }

                    else -> { /* Loading */ }
                }
            }
        }
    }
}