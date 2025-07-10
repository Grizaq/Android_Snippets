package com.chirilglance.androidglancedna.domain.utils

import com.chirilglance.androidglancedna.domain.models.CountryCode

/**
 * Provider for country codes and related utilities
 */
object CountryCodeProvider {
    private val countryCodes = listOf(
        CountryCode(name = "United Kingdom", code = "GB", dialCode = "+44", pattern = "^\\d{10}$"),
        CountryCode(name = "Australia", code = "AU", dialCode = "+61", pattern = "^\\d{9}$"),
        CountryCode(name = "Brazil", code = "BR", dialCode = "+55", pattern = "^\\d{11}$"),
        CountryCode(name = "Canada", code = "CA", dialCode = "+1", pattern = "^\\d{10}$"),
        CountryCode(name = "China", code = "CN", dialCode = "+86", pattern = "^\\d{11}$"),
        CountryCode(name = "Denmark", code = "DK", dialCode = "+45", pattern = "^\\d{8}$"),
        CountryCode(name = "France", code = "FR", dialCode = "+33", pattern = "^\\d{9}$"),
        CountryCode(name = "Germany", code = "DE", dialCode = "+49", pattern = "^\\d{11}$"),
        CountryCode(name = "Hong Kong", code = "HK", dialCode = "+852", pattern = "^\\d{8}$"),
        CountryCode(name = "India", code = "IN", dialCode = "+91", pattern = "^\\d{10}$"),
        CountryCode(name = "Ireland", code = "IE", dialCode = "+353", pattern = "^\\d{9}$"),
        CountryCode(name = "Israel", code = "IL", dialCode = "+972", pattern = "^\\d{9}$"),
        CountryCode(name = "Italy", code = "IT", dialCode = "+39", pattern = "^\\d{10}$"),
        CountryCode(name = "Japan", code = "JP", dialCode = "+81", pattern = "^\\d{10}$"),
        CountryCode(name = "Mexico", code = "MX", dialCode = "+52", pattern = "^\\d{10}$"),
        CountryCode(name = "Netherlands", code = "NL", dialCode = "+31", pattern = "^\\d{9}$"),
        CountryCode(name = "New Zealand", code = "NZ", dialCode = "+64", pattern = "^\\d{9}$"),
        CountryCode(name = "Norway", code = "NO", dialCode = "+47", pattern = "^\\d{8}$"),
        CountryCode(name = "Poland", code = "PL", dialCode = "+48", pattern = "^\\d{9}$"),
        CountryCode(name = "Russia", code = "RU", dialCode = "+7", pattern = "^\\d{10}$"),
        CountryCode(name = "Saudi Arabia", code = "SA", dialCode = "+966", pattern = "^\\d{9}$"),
        CountryCode(name = "Singapore", code = "SG", dialCode = "+65", pattern = "^\\d{8}$"),
        CountryCode(name = "South Africa", code = "ZA", dialCode = "+27", pattern = "^\\d{9}$"),
        CountryCode(name = "South Korea", code = "KR", dialCode = "+82", pattern = "^\\d{10}$"),
        CountryCode(name = "Spain", code = "ES", dialCode = "+34", pattern = "^\\d{9}$"),
        CountryCode(name = "Sweden", code = "SE", dialCode = "+46", pattern = "^\\d{9}$"),
        CountryCode(name = "Switzerland", code = "CH", dialCode = "+41", pattern = "^\\d{9}$"),
        CountryCode(name = "Thailand", code = "TH", dialCode = "+66", pattern = "^\\d{9}$"),
        CountryCode(name = "Turkey", code = "TR", dialCode = "+90", pattern = "^\\d{10}$"),
        CountryCode(name = "United States", code = "US", dialCode = "+1", pattern = "^\\d{10}$"),
    )

    /**
     * Get all available country codes
     */
    fun getAllCountryCodes(): List<CountryCode> = countryCodes

    /**
     * Get the default country (United Kingdom)
     */
    fun getDefaultCountry(): CountryCode {
        return countryCodes.find { it.code == "GB" } ?: countryCodes.first()
    }

    /**
     * Find a country by its code (e.g., "US")
     */
    fun findCountryByCode(code: String): CountryCode? {
        return countryCodes.find { it.code.equals(code, ignoreCase = true) }
    }

    /**
     * Find a country by its dial code (e.g., "+1")
     */
    fun findCountryByDialCode(dialCode: String): CountryCode? {
        return countryCodes.find { it.dialCode == dialCode }
    }

    /**
     * Convert country code to emoji flag
     */
    fun codeToEmoji(countryCode: String): String {
        // Convert country code to emoji flag
        val firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6

        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }
}