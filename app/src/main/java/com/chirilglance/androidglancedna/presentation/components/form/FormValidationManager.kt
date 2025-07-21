package com.chirilglance.androidglancedna.presentation.components.form

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.chirilglance.androidglancedna.core.validation.ValidationResult

/**
 * Form validation manager that handles validation of multiple fields
 * Reduces boilerplate in ViewModels and tracks form validation state
 */
class FormValidationManager(
    private vararg val validators: () -> ValidationResult
) {
    // Track whether validation has been triggered for the entire form
    private val _isValidated = mutableStateOf(false)
    val isValidated: State<Boolean> = _isValidated

    // Track the last validation result
    private val _isValid = mutableStateOf(false)
    val isValid: State<Boolean> = _isValid

    /**
     * Run all validators and return whether all are valid
     * Marks the form as validated regardless of result
     */
    fun validateAll(): Boolean {
        _isValidated.value = true
        val valid = validators.all { it().isValid }
        _isValid.value = valid
        return valid
    }

    /**
     * Check if all validators pass but don't update the validation state
     * Useful for enabling/disabling submit buttons
     */
    fun isFormValid(): Boolean {
        return validators.all { it().isValid }
    }

    /**
     * Reset validation state
     * Useful when clearing a form or navigating away
     */
    fun reset() {
        _isValidated.value = false
        _isValid.value = false
    }

    /**
     * Get all validation error messages
     * Useful for displaying a summary of errors
     */
    fun getErrorMessages(): List<String> {
        return validators.mapNotNull {
            val result = it()
            if (result is ValidationResult.Invalid) result.errorMessage else null
        }
    }
}