package com.chirilglance.androidglancedna.core.extension

import java.util.Locale

/**
 * Capitalize the first letter of each word in a string.
 * Useful for formatting names and titles for display.
 *
 * Example:
 * "john smith" -> "John Smith"
 * "JANE DOE" -> "Jane Doe"
 */
fun String.capitalizeWords(): String {
    return this.split(" ")
        .filter { it.isNotEmpty() }
        .joinToString(" ") { word ->
            word.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                else it.toString()
            }
        }
}

/**
 * Extract initials from a name (first letter of first and last word).
 * Useful for avatar placeholders when a profile image is not available.
 *
 * Example:
 * "John Smith" -> "JS"
 * "Jane" -> "J"
 * "John David Smith" -> "JS" (takes first and last only)
 */
fun String.extractInitials(): String {
    val parts = this.trim().split(" ").filter { it.isNotEmpty() }
    return when {
        parts.isEmpty() -> ""
        parts.size == 1 -> parts.first().take(1).uppercase()
        else -> (parts.first().take(1) + parts.last().take(1)).uppercase()
    }
}

/**
 * Check if a string contains any of the given substrings (case-insensitive).
 * Useful for simple search functionality.
 *
 * Example:
 * "Hello World".containsAny("hello", "test") -> true
 * "Hello World".containsAny("test", "example") -> false
 */
fun String.containsAny(vararg substrings: String): Boolean {
    return substrings.any { this.contains(it, ignoreCase = true) }
}

/**
 * Truncate a string to a maximum length with optional ellipsis.
 * Useful for displaying limited text in UI elements.
 *
 * Example:
 * "This is a very long text".truncate(10) -> "This is a..."
 * "Short text".truncate(20) -> "Short text" (no truncation needed)
 * "Too long".truncate(5, useEllipsis = false) -> "Too l"
 */
fun String.truncate(maxLength: Int, useEllipsis: Boolean = true): String {
    if (this.length <= maxLength) return this
    val ellipsis = if (useEllipsis) "..." else ""
    val limit = maxLength - ellipsis.length
    return this.take(limit) + ellipsis
}

/**
 * Returns a non-empty string or null if the string is empty or blank.
 * Useful for form validation and API requests.
 *
 * Example:
 * "Hello".nonEmptyOrNull() -> "Hello"
 * "".nonEmptyOrNull() -> null
 * "   ".nonEmptyOrNull() -> null
 */
fun String.nonEmptyOrNull(): String? {
    return if (this.isBlank()) null else this
}

/**
 * Converts the string to an integer or returns null if the conversion fails.
 * Useful for safe parsing of user input.
 *
 * Example:
 * "123".toIntOrNull() -> 123
 * "abc".toIntOrNull() -> null
 */
fun String.toIntSafely(): Int? {
    return try {
        this.toInt()
    } catch (e: NumberFormatException) {
        null
    }
}

/**
 * Removes all non-digit characters from a string.
 * Useful for cleaning phone numbers or numeric inputs.
 *
 * Example:
 * "+1 (123) 456-7890".digitsOnly() -> "11234567890"
 */
fun String.digitsOnly(): String {
    return this.replace(Regex("[^0-9]"), "")
}

/**
 * Formats a phone number string for display.
 * Assumes the input is digits only.
 *
 * Example:
 * "1234567890".formatPhoneNumber() -> "(123) 456-7890"
 */
fun String.formatPhoneNumber(): String {
    val digits = this.digitsOnly()
    return when {
        digits.length == 10 -> {
            val areaCode = digits.substring(0, 3)
            val firstPart = digits.substring(3, 6)
            val secondPart = digits.substring(6, 10)
            "($areaCode) $firstPart-$secondPart"
        }
        digits.length > 10 -> {
            val countryCode = digits.substring(0, digits.length - 10)
            val areaCode = digits.substring(digits.length - 10, digits.length - 7)
            val firstPart = digits.substring(digits.length - 7, digits.length - 4)
            val secondPart = digits.substring(digits.length - 4)
            "+$countryCode ($areaCode) $firstPart-$secondPart"
        }
        else -> digits // Return as is if fewer than 10 digits
    }
}