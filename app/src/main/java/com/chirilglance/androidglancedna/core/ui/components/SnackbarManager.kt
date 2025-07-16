package com.chirilglance.androidglancedna.core.ui.components

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SnackbarManager {
    private val _snackbarState = MutableStateFlow<SnackbarState>(SnackbarState.Hidden)
    val snackbarState: StateFlow<SnackbarState> = _snackbarState.asStateFlow()

    fun showError(
        message: String,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        duration: SnackbarDuration = SnackbarDuration.SHORT,
        showDismissButton: Boolean = true
    ) {
        _snackbarState.value = SnackbarState.Visible(
            message = message,
            type = SnackbarType.ERROR,
            actionLabel = actionLabel,
            onAction = onAction,
            duration = duration,
            showDismissButton = showDismissButton
        )
    }

    fun showSuccess(
        message: String,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        duration: SnackbarDuration = SnackbarDuration.SHORT,
        showDismissButton: Boolean = false
    ) {
        _snackbarState.value = SnackbarState.Visible(
            message = message,
            type = SnackbarType.SUCCESS,
            actionLabel = actionLabel,
            onAction = onAction,
            duration = duration,
            showDismissButton = showDismissButton
        )
    }

    fun showInfo(
        message: String,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        duration: SnackbarDuration = SnackbarDuration.SHORT,
        showDismissButton: Boolean = true
    ) {
        _snackbarState.value = SnackbarState.Visible(
            message = message,
            type = SnackbarType.INFO,
            actionLabel = actionLabel,
            onAction = onAction,
            duration = duration,
            showDismissButton = showDismissButton
        )
    }

    fun hideSnackbar() {
        _snackbarState.value = SnackbarState.Hidden
    }
}

sealed class SnackbarState {
    object Hidden : SnackbarState()
    data class Visible(
        val message: String,
        val type: SnackbarType,
        val actionLabel: String? = null,
        val onAction: (() -> Unit)? = null,
        val duration: SnackbarDuration,
        val showDismissButton: Boolean = true
    ) : SnackbarState()
}

enum class SnackbarType {
    ERROR, SUCCESS, INFO
}

enum class SnackbarDuration(val millis: Long) {
    SHORT(4000L),
    LONG(7000L),
    INDEFINITE(-1L)
}