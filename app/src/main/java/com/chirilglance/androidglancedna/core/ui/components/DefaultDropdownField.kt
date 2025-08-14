package com.chirilglance.androidglancedna.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

/**
 * A unified dropdown field component that allows selecting from a list of options.
 *
 * @param value Current selected value
 * @param onValueChange Callback when value changes
 * @param label Label displayed on the left side
 * @param options List of options to display in dropdown
 * @param modifier Modifier to be applied to the component
 * @param placeholder Text to show when no value is selected
 * @param errorMessage Optional error message to display
 * @param isActive Whether this field is currently active/focused
 * @param useAccentColor Whether to use the accent color for text and icon
 * @param dropdownWidth Width of the dropdown popup
 * @param dropdownMaxHeight Maximum height of the dropdown popup
 * @param enabled Whether the dropdown is enabled for interaction
 */
@Composable
fun DefaultDropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    placeholder: String = "Select option",
    errorMessage: String? = null,
    isActive: Boolean = false,
    useAccentColor: Boolean = false,
    dropdownWidth: Int = 240,
    dropdownMaxHeight: Int = 300,
    enabled: Boolean = true
) {
    // State for dropdown expansion
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Track the position of the dropdown anchor
    var dropdownAnchor by remember { mutableStateOf<Rect?>(null) }
    val density = LocalDensity.current

    // Determine text color based on state
    val textColor = when {
        useAccentColor -> MaterialTheme.colorScheme.secondary
        value.isEmpty() -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }

    val displayText = value.ifEmpty { placeholder }

    // Container that will track position for dropdown
    Box(modifier = Modifier
        .fillMaxWidth()
        .onGloballyPositioned { coordinates ->
            // Store the boundaries of this composable for positioning the dropdown
            dropdownAnchor = coordinates.boundsInRoot()
        }) {

        // Main card
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 4.dp else 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled) {
                        isDropdownExpanded = true
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            isDropdownExpanded = true
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Label part (left side)
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(0.38f)
                )

                // Value part (right side)
                Box(
                    modifier = Modifier.weight(0.62f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = displayText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .padding(end = 8.dp)
                        )

                        // Dropdown icon
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            tint = if (useAccentColor) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Error message
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .padding(top = 40.dp)
                                .align(Alignment.BottomStart)
                        )
                    }
                }
            }
        }

        // Dropdown popup
        dropdownAnchor?.let { anchor ->
            if (isDropdownExpanded) {
                // Calculate custom position
                val parentWidth = anchor.width
                val dropdownWidthPx = with(density) { dropdownWidth.dp.toPx() }
                val rightPadding = with(density) { 16.dp.toPx() }

                // Calculate x offset to align right side of dropdown from right edge
                val xOffset = parentWidth - dropdownWidthPx - rightPadding

                Popup(
                    alignment = Alignment.TopStart,
                    offset = IntOffset(
                        x = xOffset.toInt(),
                        y = anchor.height.toInt() + 4
                    ),
                    onDismissRequest = { isDropdownExpanded = false },
                    properties = PopupProperties(
                        focusable = true,
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true
                    )
                ) {
                    Card(
                        modifier = Modifier.width(dropdownWidth.dp),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = dropdownMaxHeight.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            options.forEach { option ->
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (useAccentColor) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onValueChange(option)
                                            isDropdownExpanded = false
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                )

                                if (option != options.last()) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}