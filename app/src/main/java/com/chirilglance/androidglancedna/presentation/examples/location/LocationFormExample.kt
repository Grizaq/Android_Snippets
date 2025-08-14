package com.chirilglance.androidglancedna.presentation.examples.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chirilglance.androidglancedna.core.ui.components.LocationDropdownField
import com.chirilglance.androidglancedna.presentation.examples.ui.ComponentSection
import kotlinx.coroutines.launch

/**
 * Example of a location form with validation and submit button
 */
@Composable
fun LocationFormExample() {
    val viewModel = hiltViewModel<LocationViewModel>()
    val coroutineScope = rememberCoroutineScope()

    ComponentSection(title = "Location Form with Validation") {
        Text(
            text = "Example form demonstrating location validation. The location must be selected from the dropdown.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Location field
            LocationDropdownField(
                value = viewModel.location,
                onValueChange = { viewModel.updateLocation(it) },
                onPlaceSelected = { place ->
                    coroutineScope.launch {
                        // Get full place details with coordinates
                        val result = viewModel.getPlaceDetails(place.id)
                        if (result.isSuccess) {
                            val placeWithCoords = result.getOrNull()
                            if (placeWithCoords != null) {
                                viewModel.updateLocationWithPlace(placeWithCoords)
                            }
                        }
                    }
                },
                placesSearchResult = viewModel.placesSearchResult,
                errorMessage = viewModel.locationError,
                isLoading = viewModel.isLoading,
                updateSearchQuery = { viewModel.updateLocationSearchQuery(it) },
                modifier = Modifier.fillMaxWidth()
            )

            // Display selected coordinates if available
            if (viewModel.latitude != null && viewModel.longitude != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Selected Location: ${viewModel.location}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Coordinates: Lat: ${viewModel.latitude}, Lng: ${viewModel.longitude}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Form actions
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.resetLocation() }
                    ) {
                        Text("Reset")
                    }

                    Button(
                        onClick = { viewModel.submitLocationForm() }
                    ) {
                        Text("Submit")
                    }
                }
            }

            // Form submission result
            if (viewModel.isFormSubmitted) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (viewModel.formSuccess)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (viewModel.formSuccess) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Success",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Location successfully validated!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else {
                            Text(
                                text = "Please fix the errors above and try again.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}