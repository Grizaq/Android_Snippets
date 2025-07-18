package com.chirilglance.androidglancedna.core.validation

import java.util.regex.Pattern

/**
 * Core validation utility providing common validation logic across the app
 */
object ValidationUtils {
    // Constants for validation rules
    const val MIN_NAME_LENGTH = 2
    const val MIN_BIO_LENGTH = 10
    const val MAX_BIO_LENGTH = 200
    const val MIN_LOCATION_LENGTH = 3
    const val MAX_IMAGE_SIZE_BYTES = 10 * 1024 * 1024L  // 10MB

    // Email validation pattern
    private val EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+"
    )

    /**
     * Name validation
     */
    fun validateName(name: String, fieldName: String = "Name"): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Invalid("$fieldName is required")
            name.length < MIN_NAME_LENGTH -> ValidationResult.Invalid("$fieldName is too short")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Email validation
     */
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Invalid("Email is required")
            !EMAIL_PATTERN.matcher(email).matches() -> ValidationResult.Invalid("Please enter a valid email address")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Bio validation
     */
    fun validateBio(bio: String): ValidationResult {
        return when {
            bio.isBlank() -> ValidationResult.Invalid("Bio is required")
            bio.length < MIN_BIO_LENGTH -> ValidationResult.Invalid("Bio is too short (minimum $MIN_BIO_LENGTH characters)")
            bio.length > MAX_BIO_LENGTH -> ValidationResult.Invalid("Bio must be less than $MAX_BIO_LENGTH characters")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Date validation (generic)
     */
    fun validateDate(date: String, fieldName: String = "Date"): ValidationResult {
        return if (date.isBlank()) {
            ValidationResult.Invalid("$fieldName is required")
        } else {
            ValidationResult.Valid
        }
    }

    /**
     * Location validation
     */
    fun validateLocation(location: String): ValidationResult {
        return when {
            location.isBlank() -> ValidationResult.Invalid("Location is required")
            location.length < MIN_LOCATION_LENGTH -> ValidationResult.Invalid("Please enter a valid location")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Password validation
     */
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Invalid("Password is required")
            password.length < 8 -> ValidationResult.Invalid("Password must be at least 8 characters")
            !password.contains(Regex("[A-Z]")) -> ValidationResult.Invalid("Password must contain at least one uppercase letter")
            !password.contains(Regex("[0-9]")) -> ValidationResult.Invalid("Password must contain at least one digit")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Password confirmation validation
     */
    fun validatePasswordConfirmation(password: String, confirmation: String): ValidationResult {
        return when {
            confirmation.isBlank() -> ValidationResult.Invalid("Please confirm your password")
            password != confirmation -> ValidationResult.Invalid("Passwords do not match")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Number validation
     */
    fun validateNumber(number: String, fieldName: String = "Number"): ValidationResult {
        return try {
            val parsed = number.toInt()
            if (parsed < 0) ValidationResult.Invalid("$fieldName cannot be negative")
            else ValidationResult.Valid
        } catch (e: NumberFormatException) {
            ValidationResult.Invalid("$fieldName must be a valid number")
        }
    }

    /**
     * Double validation
     */
    fun validateDouble(value: String, fieldName: String = "Double"): ValidationResult {
        return try {
            val parsed = value.toDouble()
            if (parsed < 0) ValidationResult.Invalid("$fieldName cannot be negative")
            else ValidationResult.Valid
        } catch (e: NumberFormatException) {
            ValidationResult.Invalid("$fieldName must be a valid number")
        }
    }
}