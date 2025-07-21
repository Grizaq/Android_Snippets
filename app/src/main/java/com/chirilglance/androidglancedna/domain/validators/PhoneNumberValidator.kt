package com.chirilglance.androidglancedna.domain.validators

import com.chirilglance.androidglancedna.core.validation.ValidationResult
import com.chirilglance.androidglancedna.core.validation.Validator
import com.chirilglance.androidglancedna.domain.models.CountryCode
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Validator for phone numbers that handles international formats
 */
@Singleton
class PhoneNumberValidator @Inject constructor() {

    /**
     * Validates a phone number based on the pattern for the given country
     * @param phoneNumber The phone number to validate (without country code)
     * @param countryCode The country object to validate against
     * @return ValidationResult indicating if the phone number is valid
     */
    fun validate(phoneNumber: String, countryCode: CountryCode): ValidationResult {
        // Get the clean number (digits only)
        val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")

        if (cleanNumber.isEmpty()) {
            return ValidationResult.Invalid("Phone number is required")
        }

        // Check against the country's pattern
        if (!cleanNumber.matches(Regex(countryCode.pattern))) {
            return ValidationResult.Invalid("Invalid phone number format for ${countryCode.name}")
        }

        return ValidationResult.Valid
    }

    /**
     * Legacy validation method that returns String? for backward compatibility
     * Used in the PhoneVerificationViewModel
     */
    fun validatePhoneNumber(phoneNumber: String, countryCode: CountryCode): String? {
        val result = validate(phoneNumber, countryCode)
        return if (result is ValidationResult.Invalid) result.message else null
    }

    /**
     * Creates a validator object for the given country code
     */
    fun createValidator(countryCodeGetter: () -> CountryCode): Validator<String> {
        return object : Validator<String> {
            override fun validate(value: String): ValidationResult {
                return this@PhoneNumberValidator.validate(value, countryCodeGetter())
            }
        }
    }

    /**
     * Formats a phone number for API submission
     * @param phoneNumber The phone number without country code
     * @param countryCode The country object
     * @return Formatted phone number with country code for API submission
     */
    fun formatForApi(phoneNumber: String, countryCode: CountryCode): String {
        // Remove all non-digit characters
        val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")
        return "${countryCode.dialCode}$cleanNumber"
    }

    /**
     * Formats a phone number for display
     * @param phoneNumber The phone number without country code
     * @param countryCode The country object
     * @return Formatted phone number for display
     */
    fun formatForDisplay(phoneNumber: String, countryCode: CountryCode): String {
        val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")

        // Apply country-specific formatting if available
        // For now, just add a space after country code
        return "${countryCode.dialCode} $cleanNumber"
    }
}