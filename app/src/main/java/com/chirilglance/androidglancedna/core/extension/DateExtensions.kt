package com.chirilglance.androidglancedna.core.extension

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Date and time formatting constants
 */
object DateFormatters {
    // API date formatters (ISO 8601)
    private val API_DATE_FORMATTER = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    // Display formatters
    private val DISPLAY_DATE_FORMATTER = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    private val SHORT_DATE_FORMATTER = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    private val TIME_FORMATTER = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    private val DATE_TIME_FORMATTER = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH)

    // LocalDate formatters
    private val LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    private val LOCAL_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
    private val LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)

    /**
     * Get the API date formatter (UTC timezone)
     */
    fun getApiDateFormatter(): SimpleDateFormat = API_DATE_FORMATTER

    /**
     * Get the display date formatter
     */
    fun getDateFormatter(): SimpleDateFormat = DISPLAY_DATE_FORMATTER

    /**
     * Get the short date formatter (dd/MM/yyyy)
     */
    fun getShortDateFormatter(): SimpleDateFormat = SHORT_DATE_FORMATTER

    /**
     * Get the time formatter
     */
    fun getTimeFormatter(): SimpleDateFormat = TIME_FORMATTER

    /**
     * Get the date time formatter
     */
    fun getDateTimeFormatter(): SimpleDateFormat = DATE_TIME_FORMATTER

    /**
     * Get the LocalDate formatter
     */
    fun getLocalDateFormatter(): DateTimeFormatter = LOCAL_DATE_FORMATTER

    /**
     * Get the LocalTime formatter
     */
    fun getLocalTimeFormatter(): DateTimeFormatter = LOCAL_TIME_FORMATTER

    /**
     * Get the LocalDateTime formatter
     */
    fun getLocalDateTimeFormatter(): DateTimeFormatter = LOCAL_DATE_TIME_FORMATTER
}

// --- Date Extensions ---

/**
 * Format a Date for display using the standard date formatter (dd MMM yyyy)
 *
 * Example:
 * Date().formatForDisplay() -> "21 Jul 2025"
 */
fun Date.formatForDisplay(): String {
    return DateFormatters.getDateFormatter().format(this)
}

/**
 * Format a Date for API submission in ISO 8601 format
 *
 * Example:
 * Date().formatForApi() -> "2025-07-21T12:30:45.123Z"
 */
fun Date.formatForApi(): String {
    return DateFormatters.getApiDateFormatter().format(this)
}

/**
 * Format a Date with both date and time
 *
 * Example:
 * Date().formatWithTime() -> "21 Jul 2025, 12:30"
 */
fun Date.formatWithTime(): String {
    return DateFormatters.getDateTimeFormatter().format(this)
}

/**
 * Get the start of the day (00:00:00) for this Date
 */
fun Date.startOfDay(): Date {
    return Calendar.getInstance().apply {
        time = this@startOfDay
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}

/**
 * Get the end of the day (23:59:59) for this Date
 */
fun Date.endOfDay(): Date {
    return Calendar.getInstance().apply {
        time = this@endOfDay
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.time
}

/**
 * Add days to a Date
 *
 * Example:
 * Date().plusDays(5) -> Date 5 days in the future
 * Date().plusDays(-2) -> Date 2 days in the past
 */
fun Date.plusDays(days: Int): Date {
    return Calendar.getInstance().apply {
        time = this@plusDays
        add(Calendar.DAY_OF_MONTH, days)
    }.time
}

/**
 * Check if a Date is today
 *
 * Example:
 * Date().isToday() -> true/false
 */
fun Date.isToday(): Boolean {
    val today = Calendar.getInstance()
    val thisDate = Calendar.getInstance().apply { time = this@isToday }
    return today.get(Calendar.YEAR) == thisDate.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == thisDate.get(Calendar.DAY_OF_YEAR)
}

/**
 * Format a Date to display how long ago it was (relative time)
 *
 * Examples:
 * - Just now (less than a minute ago)
 * - 5m ago (minutes)
 * - 2h ago (hours)
 * - Yesterday
 * - 3d ago (days up to a week)
 * - Aug 15 (same year)
 * - Aug 15, 2024 (different year)
 */
fun Date.timeAgoDisplay(): String {
    val now = Date()
    val calendar = Calendar.getInstance()

    // Check if the date is in the future
    if (this.after(now)) return "Now"

    val diffMillis = now.time - this.time
    val seconds = diffMillis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    // Just now - within the last minute
    if (seconds < 60 && minutes == 0L && hours == 0L && days == 0L) {
        return "Now"
    }

    // Minutes
    if (minutes < 60 && hours == 0L && days == 0L) {
        return if (minutes == 1L) "1m ago" else "${minutes}m ago"
    }

    // Hours
    if (hours < 24 && days == 0L) {
        return if (hours == 1L) "1h ago" else "${hours}h ago"
    }

    val selfCal = Calendar.getInstance().apply { time = this@timeAgoDisplay }
    val nowCal = Calendar.getInstance().apply { time = now }

    // Check for yesterday
    nowCal.add(Calendar.DAY_OF_YEAR, -1)
    val isYesterday = selfCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
            selfCal.get(Calendar.DAY_OF_YEAR) == nowCal.get(Calendar.DAY_OF_YEAR)
    if (isYesterday) {
        return "Yesterday"
    }

    // Days up to a week
    if (days < 7) {
        return if (days == 1L) "1d ago" else "${days}d ago"
    }

    // Same year (show month and day)
    val thisYear = calendar.get(Calendar.YEAR)
    calendar.time = this
    val inputYear = calendar.get(Calendar.YEAR)
    return if (inputYear == thisYear) {
        val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
        formatter.format(this)
    } else {
        val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        formatter.format(this)
    }
}

// --- LocalDate Extensions ---

/**
 * Convert LocalDate to Date
 */
fun LocalDate.toDate(): Date {
    return Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
}

/**
 * Convert LocalDateTime to Date
 */
fun LocalDateTime.toDate(): Date {
    return Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
}

/**
 * Convert Date to LocalDate
 */
fun Date.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this.time)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

/**
 * Convert Date to LocalDateTime
 */
fun Date.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochMilli(this.time)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}

/**
 * Format LocalDate for display
 */
fun LocalDate.formatForDisplay(): String {
    return this.format(DateFormatters.getLocalDateFormatter())
}

/**
 * Format LocalTime for display
 */
fun LocalTime.formatForDisplay(): String {
    return this.format(DateFormatters.getLocalTimeFormatter())
}

/**
 * Format LocalDateTime for display
 */
fun LocalDateTime.formatForDisplay(): String {
    return this.format(DateFormatters.getLocalDateTimeFormatter())
}

/**
 * Check if a LocalDate is today
 */
fun LocalDate.isToday(): Boolean {
    return this == LocalDate.now()
}

/**
 * Get age from birth date
 */
fun LocalDate.getAge(): Int {
    val currentDate = LocalDate.now()
    var age = currentDate.year - this.year
    if (currentDate.monthValue < this.monthValue ||
        (currentDate.monthValue == this.monthValue && currentDate.dayOfMonth < this.dayOfMonth)) {
        age--
    }
    return age
}

/**
 * Convert ISO string to LocalTime.
 * Parses a time string in HH:mm format.
 * Returns null if parsing fails.
 *
 * Example:
 * ```
 * // Parse a time string safely
 * val time = "13:45".toLocalTime()
 * // time will be a LocalTime of 1:45 PM or null if format is invalid
 * ```
 */
fun String.toLocalTime(): LocalTime? {
    return try {
        LocalTime.parse(this, DateTimeFormatter.ofPattern("HH:mm"))
    } catch (_: Exception) {
        null
    }
}

/**
 * Format LocalTime to a string with custom pattern.
 * Uses the specified pattern or defaults to HH:mm.
 *
 * Example:
 * ```
 * // Format a LocalTime with a custom pattern
 * val time = LocalTime.now().format("h:mm a")
 * // time might be "3:45 PM"
 * ```
 */
fun LocalTime.format(pattern: String = "HH:mm"): String {
    return this.format(DateTimeFormatter.ofPattern(pattern, Locale.getDefault()))
}

/**
 * Format time string from one pattern to another.
 * Useful for converting between time formats.
 * Returns null if parsing fails.
 *
 * Example:
 * ```
 * // Convert from 24-hour to 12-hour format
 * val time = "14:30".formatTime("HH:mm", "h:mm a")
 * // time will be "2:30 PM" or null if parsing fails
 * ```
 */
fun String.formatTime(
    inputPattern: String = "HH:mm",
    outputPattern: String = "h:mm a"
): String? {
    return try {
        val time = LocalTime.parse(this, DateTimeFormatter.ofPattern(inputPattern))
        time.format(DateTimeFormatter.ofPattern(outputPattern, Locale.getDefault()))
    } catch (_: Exception) {
        null
    }
}