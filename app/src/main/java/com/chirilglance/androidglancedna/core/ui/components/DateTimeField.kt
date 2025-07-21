package com.chirilglance.androidglancedna.core.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chirilglance.androidglancedna.presentation.ui.theme.ClubConnectGreen
import com.chirilglance.androidglancedna.presentation.ui.theme.HintColor
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

/**
 * A form field for date and optional time selection
 * Shows selected date/time in pill format and opens date/time pickers on click
 */
@Composable
fun DateTimeField(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "Select date",
    errorMessage: String? = null,
    includeTime: Boolean = false,
    selectedTime: LocalTime? = null,
    onTimeSelected: ((LocalTime) -> Unit)? = null,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    timeRangeStart: LocalTime? = null,
    timeRangeEnd: LocalTime? = null,
    dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy"),
    timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm"), // Using 24h format
    isFocused: Boolean = false
) {
    // Create date picker dialog
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Set initial date to selected date or current date
    if (selectedDate != null) {
        calendar.set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)
    }

    // Format the selected date and time for display
    val dateText = selectedDate?.format(dateFormatter) ?: ""
    val timeText =
        if (includeTime && selectedTime != null) selectedTime.format(timeFormatter) else ""

    // Create date picker dialog
    val datePickerDialog = remember(calendar, minDate, maxDate) {
        val dialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newDate = LocalDate.of(year, month + 1, dayOfMonth)
                onDateSelected(newDate)

                // Show time picker if time is included
                if (includeTime && onTimeSelected != null) {
                    val initialHour = selectedTime?.hour ?: 12
                    val initialMinute = selectedTime?.minute ?: 0

                    TimePickerDialog(
                        context, { _, hourOfDay, minute ->
                            onTimeSelected(LocalTime.of(hourOfDay, minute))
                        }, initialHour, initialMinute, true // 24-hour format
                    ).show()
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set min date if specified
        if (minDate != null) {
            val minCalendar = Calendar.getInstance()
            minCalendar.set(minDate.year, minDate.monthValue - 1, minDate.dayOfMonth)
            dialog.datePicker.minDate = minCalendar.timeInMillis
        }

        // Set max date if specified
        if (maxDate != null) {
            val maxCalendar = Calendar.getInstance()
            maxCalendar.set(maxDate.year, maxDate.monthValue - 1, maxDate.dayOfMonth)
            dialog.datePicker.maxDate = maxCalendar.timeInMillis
        }

        dialog
    }

    // Card with highlight when focused
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused) ClubConnectGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // Match standard text field height
                .clickable { datePickerDialog.show() }
                .padding(horizontal = 16.dp, vertical = 0.dp),
                verticalAlignment = Alignment.CenterVertically) {
                // Label part (left side)
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(0.4f)
                )

                // Value part (right side) - right aligned
                Box(
                    modifier = Modifier.weight(0.6f), contentAlignment = Alignment.CenterEnd
                ) {
                    if (dateText.isNotEmpty() || timeText.isNotEmpty()) {
                        // Pill layout for date and optional time
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (dateText.isNotEmpty()) {
                                DateTimePill(text = dateText)
                            }

                            if (includeTime && timeText.isNotEmpty()) {
                                DateTimePill(
                                    text = timeText, modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    } else {
                        // Show hint when no date/time is selected
                        Text(
                            text = hint,
                            color = HintColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Error message
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Pill component for displaying date or time
 */
@Composable
private fun DateTimePill(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        shape = RoundedCornerShape(16.dp), color = backgroundColor, modifier = modifier
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Specialized date field for birth dates
 */
@Composable
fun DateOfBirthField(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Date of Birth",
    hint: String = "Select birth date",
    errorMessage: String? = null,
    isFocused: Boolean = false
) {
    // Calculate reasonable min/max dates for birth dates
    val currentDate = LocalDate.now()
    val minDate = currentDate.minusYears(100) // 100 years ago
    val maxDate = currentDate.minusDays(1) // Yesterday at the latest

    DateTimeField(
        label = label,
        selectedDate = selectedDate,
        onDateSelected = onDateSelected,
        modifier = modifier,
        hint = hint,
        errorMessage = errorMessage,
        includeTime = false, // Birth dates typically don't include time
        minDate = minDate,
        maxDate = maxDate,
        isFocused = isFocused
    )
}

/**
 * Date field for event scheduling with optional time
 */
@Composable
fun EventDateField(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Event Date",
    hint: String = "Select event date",
    errorMessage: String? = null,
    includeTime: Boolean = true,
    selectedTime: LocalTime? = null,
    onTimeSelected: ((LocalTime) -> Unit)? = null,
    allowPastDates: Boolean = false,
    futureMonthLimit: Int = 12, // How many months into the future to allow
    timeRangeStart: LocalTime? = LocalTime.of(8, 0), // Default 8 AM start
    timeRangeEnd: LocalTime? = LocalTime.of(20, 0), // Default 8 PM end
    minDate: LocalDate? = null, // Allow overriding min date
    isFocused: Boolean = false
) {
    // Calculate reasonable min/max dates for events
    val currentDate = LocalDate.now()

    // Use provided minDate or calculate based on allowPastDates
    val effectiveMinDate = minDate ?: if (!allowPastDates) currentDate else null

    // Calculate max date if futureMonthLimit is set
    val maxDate = if (futureMonthLimit > 0) currentDate.plusMonths(futureMonthLimit.toLong())
    else null

    DateTimeField(
        label = label,
        selectedDate = selectedDate,
        onDateSelected = onDateSelected,
        modifier = modifier,
        hint = hint,
        errorMessage = errorMessage,
        includeTime = includeTime,
        selectedTime = selectedTime,
        onTimeSelected = onTimeSelected,
        minDate = effectiveMinDate,
        maxDate = maxDate,
        timeRangeStart = timeRangeStart,
        timeRangeEnd = timeRangeEnd,
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm"), // Using 24h format
        isFocused = isFocused
    )
}