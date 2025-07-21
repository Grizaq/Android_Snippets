package com.chirilglance.androidglancedna.domain.validators

import com.chirilglance.androidglancedna.domain.models.CountryCode
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Validator for phone numbers that handles international formats
 */
@Singleton
class PhoneNumberFormatter @Inject constructor() {

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