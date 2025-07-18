package com.chirilglance.androidglancedna.core.validation

/**
 * Standard validation result for all validation operations
 * Used to represent the outcome of a validation check
 */
sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()

    val isValid: Boolean get() = this is Valid
    val errorMessage: String? get() = if (this is Invalid) message else null

    companion object {
        /**
         * Helper to validate non-blank fields
         */
        fun validateNonBlank(value: String, fieldName: String): ValidationResult {
            return if (value.isBlank()) {
                Invalid("$fieldName is required")
            } else {
                Valid
            }
        }
    }
}