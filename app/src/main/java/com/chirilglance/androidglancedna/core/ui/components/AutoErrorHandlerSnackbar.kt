package com.chirilglance.androidglancedna.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.chirilglance.androidglancedna.core.domain.model.UiState

@Composable
fun <T> AutoErrorHandler(
    state: UiState<T>,
    onRetry: (() -> Unit)? = null,
    onSuccess: ((T) -> Unit)? = null,
    enabled: Boolean = true
) {
    // Only handle errors automatically if enabled
    if (enabled) {
        LaunchedEffect(state) {
            when (state) {
                is UiState.Error -> {
                    SnackbarManager.showError(
                        message = state.message,
                        actionLabel = if (onRetry != null) "Retry" else null,
                        onAction = onRetry
                    )
                }
                is UiState.Success -> {
                    onSuccess?.invoke(state.data)
                }
                else -> { /* No action needed */ }
            }
        }
    }
}