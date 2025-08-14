package com.chirilglance.androidglancedna.presentation.examples.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chirilglance.androidglancedna.core.ui.components.LabeledLocationField
import com.chirilglance.androidglancedna.core.ui.components.LocationDropdownField
import com.chirilglance.androidglancedna.presentation.examples.ui.ComponentSection
import kotlinx.coroutines.launch

@Composable
fun LocationServiceScreen() {
    val viewModel = hiltViewModel<LocationViewModel>()
    val coroutineScope = rememberCoroutineScope()

    // State for the fields
    var standardLocation by remember { mutableStateOf("") }
    var standardCoordinates by remember { mutableStateOf("") }
    var labeledLocation by remember { mutableStateOf("") }
    var labeledCoordinates by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Text(
            text = "Location Services",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Components and utilities for handling location data with Google Places API integration.",
            style = MaterialTheme.typography.bodyLarge
        )

        // Location Form with Validation
        LocationFormExample()

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Standard Location Field
        ComponentSection(title = "Standard Location Field") {
            Text(
                text = "Full-width location field with Google Places autocomplete.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LocationDropdownField(
                value = standardLocation,
                onValueChange = { standardLocation = it },
                onPlaceSelected = { place ->
                    coroutineScope.launch {
                        // Get full place details with coordinates
                        val result = viewModel.getPlaceDetails(place.id)
                        if (result.isSuccess) {
                            val placeWithCoords = result.getOrNull()
                            if (placeWithCoords != null) {
                                standardLocation = placeWithCoords.fullText
                                standardCoordinates = "Lat: ${placeWithCoords.latitude}, Lng: ${placeWithCoords.longitude}"
                            }
                        }
                    }
                },
                placesSearchResult = viewModel.placesSearchResult,
                errorMessage = null, // No validation in this example
                isLoading = viewModel.isLoading,
                updateSearchQuery = { viewModel.updateLocationSearchQuery(it) },
                modifier = Modifier.fillMaxWidth()
            )

            if (standardCoordinates.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Selected Location",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = standardLocation,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = standardCoordinates,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Labeled Location Field
        ComponentSection(title = "Labeled Location Field") {
            Text(
                text = "Card-based location field with label and input side by side.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LabeledLocationField(
                value = labeledLocation,
                onValueChange = { labeledLocation = it },
                onPlaceSelected = { place ->
                    coroutineScope.launch {
                        // Get full place details with coordinates
                        val result = viewModel.getPlaceDetails(place.id)
                        if (result.isSuccess) {
                            val placeWithCoords = result.getOrNull()
                            if (placeWithCoords != null) {
                                labeledLocation = placeWithCoords.fullText
                                labeledCoordinates = "Lat: ${placeWithCoords.latitude}, Lng: ${placeWithCoords.longitude}"
                            }
                        }
                    }
                },
                placesSearchResult = viewModel.placesSearchResult,
                errorMessage = null, // No validation in this example
                isLoading = viewModel.isLoading,
                updateSearchQuery = { viewModel.updateLocationSearchQuery(it) },
                modifier = Modifier.fillMaxWidth()
            )

            if (labeledCoordinates.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Selected Location",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = labeledLocation,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = labeledCoordinates,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Usage Instructions
        ComponentSection(title = "Implementation Notes") {
            Text(
                text = "To use these components in your app:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "1. Make sure you have added your Google Places API key to the AndroidManifest.xml\n" +
                        "2. Inject the LocationViewModel in your screen\n" +
                        "3. Add the appropriate location field component to your UI\n" +
                        "4. Handle place selection to get coordinates\n" +
                        "5. Use the validation functionality for form submissions",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}