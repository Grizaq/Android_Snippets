package com.chirilglance.androidglancedna.presentation.examples.location

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chirilglance.androidglancedna.core.validation.ValidationUtils
import com.chirilglance.androidglancedna.domain.models.Place
import com.chirilglance.androidglancedna.domain.repository.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for handling location-related operations
 */
@HiltViewModel
class LocationViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    // Location state
    var location by mutableStateOf("")
        private set

    var locationError by mutableStateOf<String?>(null)
        private set

    var latitude by mutableStateOf<Double?>(null)
        private set

    var longitude by mutableStateOf<Double?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    // Form submission state
    var isFormSubmitted by mutableStateOf(false)
        private set

    var formSuccess by mutableStateOf(false)
        private set

    // Track if the location was selected from dropdown
    var isLocationSelectedFromDropdown by mutableStateOf(false)
        private set

    // Places search result
    private val _placesSearchResult = MutableStateFlow<Result<List<Place>>>(Result.success(emptyList()))
    val placesSearchResult: StateFlow<Result<List<Place>>> = _placesSearchResult.asStateFlow()

    /**
     * Update location search query
     */
    fun updateLocationSearchQuery(query: String) {
        if (query.length < 2) {
            _placesSearchResult.value = Result.success(emptyList())
            return
        }

        viewModelScope.launch {
            isLoading = true

            placesRepository.searchPlaces(query)
                .catch { e ->
                    _placesSearchResult.value = Result.failure(e)
                    isLoading = false
                }
                .collect { result ->
                    _placesSearchResult.value = result
                    isLoading = false
                }
        }
    }

    /**
     * Update location with selected place
     */
    fun updateLocationWithPlace(place: Place) {
        location = place.fullText
        latitude = place.latitude
        longitude = place.longitude
        isLocationSelectedFromDropdown = true
        validateLocation()
    }

    /**
     * Update location text
     * When typing manually, clear selected status
     */
    fun updateLocation(value: String) {
        location = value
        // When manually typing, clear the selected flag and coordinates
        if (isLocationSelectedFromDropdown) {
            isLocationSelectedFromDropdown = false
            latitude = null
            longitude = null
        }
        validateLocation()
    }

    /**
     * Get detailed place information
     */
    suspend fun getPlaceDetails(placeId: String): Result<Place> {
        isLoading = true
        return try {
            val result = placesRepository.getPlaceDetails(placeId)
            isLoading = false
            result
        } catch (e: Exception) {
            isLoading = false
            Result.failure(e)
        }
    }

    /**
     * Validate location field
     * We require both:
     * 1. A non-empty, valid location string
     * 2. The location must have been selected from the dropdown
     */
    fun validateLocation(): Boolean {
        // First validate the basic text
        val baseValidation = ValidationUtils.validateLocation(location)

        // If the basic validation fails, show that error
        if (!baseValidation.isValid) {
            locationError = baseValidation.errorMessage
            return false
        }

        // If not selected from dropdown, show error
        if (!isLocationSelectedFromDropdown) {
            locationError = "Please select a location from the dropdown"
            return false
        }

        // If latitude or longitude is null, show error
        if (latitude == null || longitude == null) {
            locationError = "Location coordinates are required"
            return false
        }

        // All checks passed
        locationError = null
        return true
    }

    /**
     * Submit the location form
     */
    fun submitLocationForm(): Boolean {
        val isValid = validateLocation()

        isFormSubmitted = true
        formSuccess = isValid

        return isValid
    }

    /**
     * Reset location data
     */
    fun resetLocation() {
        location = ""
        latitude = null
        longitude = null
        locationError = null
        isLocationSelectedFromDropdown = false
        isFormSubmitted = false
        formSuccess = false
    }
}