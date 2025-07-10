package com.chirilglance.androidglancedna.domain.models

/**
 * Represents a country code for phone number validation
 */
data class CountryCode(
    val name: String,
    val code: String,
    val dialCode: String,
    val pattern: String
)