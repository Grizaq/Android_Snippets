package com.chirilglance.androidglancedna.domain.utils

import com.chirilglance.androidglancedna.domain.models.CountryCode
import javax.inject.Inject

/**
 * Utility class for validating phone numbers
 */
class PhoneNumberValidator @Inject constructor() {

    /**
     * Validates a phone number based on the pattern for the given country
     *
     * @param phoneNumber The phone number to validate (without country code)
     * @param countryCode The country object to validate against
     * @return Error message if invalid, null if valid
     */
    fun validatePhoneNumber(phoneNumber: String, countryCode: CountryCode): String? {
        // Get the clean number (digits only)
        val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")

        if (cleanNumber.isEmpty()) {
            return "Phone number is required"
        }

        // Check against the country's pattern
        if (!cleanNumber.matches(Regex(countryCode.pattern))) {
            return "Invalid phone number format for ${countryCode.name}"
        }

        return null // Valid
    }

    /**
     * Formats a phone number for API submission
     *
     * @param phoneNumber The phone number without country code
     * @param countryCode The country object
     * @return Formatted phone number with country code for API submission
     */
    fun formatForApi(phoneNumber: String, countryCode: CountryCode): String {
        // Remove all non-digit characters
        val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")
        // Combine country code and number
        return "${countryCode.dialCode}$cleanNumber"
    }

    /**
     * Formats a phone number for display
     *
     * @param phoneNumber The phone number without country code
     * @param countryCode The country object
     * @return Formatted phone number for display
     */
    fun formatForDisplay(phoneNumber: String, countryCode: CountryCode): String {
        // Remove all non-digit characters
        val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")

        // Format based on country
        return when (countryCode.code) {
            "US", "CA" -> {
                if (cleanNumber.length == 10) {
                    "(${cleanNumber.substring(0, 3)}) ${cleanNumber.substring(3, 6)}-${cleanNumber.substring(6)}"
                } else {
                    cleanNumber
                }
            }
            "GB" -> {
                if (cleanNumber.length == 10) {
                    "${cleanNumber.substring(0, 4)} ${cleanNumber.substring(4, 7)} ${cleanNumber.substring(7)}"
                } else {
                    cleanNumber
                }
            }
            else -> cleanNumber
        }
    }
}