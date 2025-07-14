package com.chirilglance.androidglancedna.presentation.examples.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.SnackbarDuration
import com.chirilglance.androidglancedna.core.ui.extensions.showErrorSnackbar
import com.chirilglance.androidglancedna.core.ui.extensions.showInfoSnackbar
import com.chirilglance.androidglancedna.core.ui.extensions.showSuccessSnackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UIComponentsViewModel @Inject constructor() : ViewModel() {

    // State for demonstrating UiState with Snackbar
    private val _demoState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val demoState: StateFlow<UiState<String>> = _demoState.asStateFlow()

    // Basic Snackbar examples
    fun showBasicErrorSnackbar() {
        showErrorSnackbar(
            message = "This is a basic error message"
        )
    }

    fun showBasicSuccessSnackbar() {
        showSuccessSnackbar(
            message = "Operation completed successfully!"
        )
    }

    fun showBasicInfoSnackbar() {
        showInfoSnackbar(
            message = "Did you know? You can customize Snackbar duration"
        )
    }

    // Advanced Snackbar examples
    fun showErrorWithAction() {
        showErrorSnackbar(
            message = "Failed to save data",
            actionLabel = "Retry",
            onAction = { showSuccessSnackbar("Retry operation started") }
        )
    }

    fun showLongDurationSnackbar() {
        showInfoSnackbar(
            message = "This Snackbar will stay visible for a longer time",
            duration = SnackbarDuration.LONG
        )
    }

    fun showIndefiniteSnackbar() {
        showInfoSnackbar(
            message = "This Snackbar won't dismiss automatically. Click the X to close.",
            duration = SnackbarDuration.INDEFINITE,
            showDismissButton = true
        )
    }

    // UiState integration examples
    fun simulateLoading() {
        viewModelScope.launch {
            _demoState.value = UiState.Loading
            delay(1500)
            _demoState.value = UiState.Success("Data loaded successfully!")
        }
    }

    fun simulateError() {
        viewModelScope.launch {
            _demoState.value = UiState.Loading
            delay(1500)
            _demoState.value = UiState.Error("Failed to load data. Network error.")
        }
    }

    fun resetDemoState() {
        _demoState.value = UiState.Empty
    }
}