package com.chirilglance.androidglancedna.core.ui.extensions

import androidx.lifecycle.ViewModel
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.SnackbarDuration
import com.chirilglance.androidglancedna.core.ui.components.SnackbarManager

// Extension functions for ViewModels
fun ViewModel.showErrorSnackbar(
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    duration: SnackbarDuration = SnackbarDuration.SHORT,
    showDismissButton: Boolean = true
) {
    SnackbarManager.showError(message, actionLabel, onAction, duration, showDismissButton)
}

fun ViewModel.showSuccessSnackbar(
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    duration: SnackbarDuration = SnackbarDuration.SHORT,
    showDismissButton: Boolean = false
) {
    SnackbarManager.showSuccess(message, actionLabel, onAction, duration, showDismissButton)
}

fun ViewModel.showInfoSnackbar(
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    duration: SnackbarDuration = SnackbarDuration.SHORT,
    showDismissButton: Boolean = true
) {
    SnackbarManager.showInfo(message, actionLabel, onAction, duration, showDismissButton)
}

// For handling UiState.Error automatically
fun <T> ViewModel.handleErrorState(state: UiState<T>, retryAction: (() -> Unit)? = null) {
    if (state is UiState.Error) {
        showErrorSnackbar(
            message = state.message,
            actionLabel = if (retryAction != null) "Retry" else null,
            onAction = retryAction
        )
    }
}