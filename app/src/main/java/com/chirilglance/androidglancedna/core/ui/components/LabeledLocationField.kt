package com.chirilglance.androidglancedna.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.chirilglance.androidglancedna.domain.models.Place
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce

/**
 * A location field in card layout with label and input side by side.
 * Includes dropdown for location search results.
 */
@Composable
fun LabeledLocationField(
    value: String,
    onValueChange: (String) -> Unit,
    onPlaceSelected: (Place) -> Unit,
    placesSearchResult: StateFlow<Result<List<Place>>>,
    modifier: Modifier = Modifier,
    label: String = "Location:",
    hint: String = "Enter location...",
    errorMessage: String? = null,
    isLoading: Boolean = false,
    isActive: Boolean = false,
    minSearchLength: Int = 2,
    updateSearchQuery: (String) -> Unit,
    labelWeight: Float = 0.3f,
    fieldWeight: Float = 0.7f
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Track field state
    var isFocused by remember { mutableStateOf(false) }
    var locationQuery by remember { mutableStateOf(value) }
    var isDropdownVisible by remember { mutableStateOf(false) }
    var fieldBounds by remember { mutableStateOf<Rect?>(null) }
    val isSelectionFromDropdown = remember { mutableStateOf(false) }

    // Create a debounced query state
    val searchQueryFlow = remember { MutableStateFlow("") }

    // Update the local query when value changes
    LaunchedEffect(value) {
        locationQuery = value
    }

    // Handle debounced search
    @OptIn(FlowPreview::class) LaunchedEffect(Unit) {
        searchQueryFlow.debounce(500) // 500ms debounce
            .collect { query ->
                // Only search if not from dropdown selection and has minimum length
                if (query.length >= minSearchLength && !isSelectionFromDropdown.value) {
                    updateSearchQuery(query)
                    isDropdownVisible = true
                }

                // Reset flag after debounce period
                isSelectionFromDropdown.value = false
            }
    }

    // Update the flow when the query changes
    LaunchedEffect(locationQuery) {
        if (locationQuery.length >= minSearchLength) {
            searchQueryFlow.value = locationQuery
        } else {
            isDropdownVisible = false
        }
    }

    Box(modifier = modifier
        .fillMaxWidth()
        .onGloballyPositioned { coordinates ->
            fieldBounds = coordinates.boundsInRoot()
        }) {
        // Labeled text field
        LabeledTextField(
            value = locationQuery,
            onValueChange = { newQuery ->
                locationQuery = newQuery
                onValueChange(newQuery)
            },
            label = label,
            hint = hint,
            errorMessage = errorMessage,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search,
            labelWeight = labelWeight,
            fieldWeight = fieldWeight,
            isFocused = isFocused || isActive,
            onFocusChanged = { focused ->
                isFocused = focused
                if (focused && locationQuery.length >= minSearchLength) {
                    isDropdownVisible = true
                }
            },
            onDone = {
                focusManager.clearFocus()
            },
            // Custom modification for the TextField to add icons
            visualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 16.dp, vertical = 8.dp
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Add search/loading icon on the right side of the card
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .zIndex(2f)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxWidth(0.05f),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Dropdown popup
        if (isDropdownVisible && fieldBounds != null) {
            LabeledLocationDropdown(query = locationQuery,
                placesSearchResult = placesSearchResult,
                fieldBounds = fieldBounds!!,
                onPlaceSelected = { place ->
                    // Set selection flag to prevent triggering another search
                    isSelectionFromDropdown.value = true

                    // Update the field value with the selected place
                    locationQuery = place.fullText
                    onValueChange(place.fullText)
                    onPlaceSelected(place)
                    isDropdownVisible = false

                    // Hide keyboard and clear focus
                    keyboardController?.hide()
                    focusManager.clearFocus()
                },
                onDismiss = {
                    isDropdownVisible = false
                })
        }
    }
}

/**
 * Dropdown component for labeled location field
 */
@Composable
private fun LabeledLocationDropdown(
    query: String,
    placesSearchResult: StateFlow<Result<List<Place>>>,
    fieldBounds: Rect,
    onPlaceSelected: (Place) -> Unit,
    onDismiss: () -> Unit
) {
    val density = LocalDensity.current
    val dropdownHeight = 240.dp

    // Get information about the root view's insets
    val rootView = LocalView.current
    val rootWindowInsets = ViewCompat.getRootWindowInsets(rootView)

    // Decide whether to show dropdown above or below
    val isKeyboardVisible = rootWindowInsets?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
    val showAbove = isKeyboardVisible

    // Get the state of places
    val placesResult by placesSearchResult.collectAsState(initial = Result.success(emptyList()))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(10f) // Ensure it's above other content
    ) {
        // Calculate appropriate offset for the popup
        val offsetY = if (showAbove) {
            // Position above the field (negative offset)
            with(density) { -dropdownHeight.toPx().toInt() - 4 }
        } else {
            // Position below the field
            fieldBounds.height.toInt() + 4
        }

        Popup(
            alignment = Alignment.TopCenter,
            offset = IntOffset(0, offsetY),
            onDismissRequest = onDismiss,
            properties = PopupProperties(
                focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.85f),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = dropdownHeight)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (query.length < 2) {
                        Text(
                            text = "Enter at least 2 characters to search",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else if (placesResult.isSuccess) {
                        val places = placesResult.getOrNull() ?: emptyList()

                        if (places.isEmpty()) {
                            Text(
                                text = "Searching for '$query'...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        } else {
                            places.forEachIndexed { index, place ->
                                LabeledLocationPlaceItem(
                                    place = place,
                                    onClick = { onPlaceSelected(place) })

                                if (index < places.size - 1) {
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    } else if (placesResult.isFailure) {
                        Text(
                            text = "Error searching places. Please try again.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual place item component for labeled location dropdown
 */
@Composable
private fun LabeledLocationPlaceItem(
    place: Place, onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = place.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = place.address,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}