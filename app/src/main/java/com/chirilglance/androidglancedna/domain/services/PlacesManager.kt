package com.chirilglance.androidglancedna.domain.services

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.chirilglance.androidglancedna.domain.models.Place
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Manager class for Places API operations
 * Handles initialization and provides access to Places functionality
 */
@Singleton
class PlacesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val placesClient: PlacesClient
    private var sessionToken: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()

    init {
        // Get API key from manifest metadata
        val apiKey = getApiKeyFromManifest(context)

        // Initialize Places if not already initialized
        if (!Places.isInitialized()) {
            Places.initialize(context, apiKey)
        }
        placesClient = Places.createClient(context)
    }

    /**
     * Get API key from manifest metadata
     */
    private fun getApiKeyFromManifest(context: Context): String {
        try {
            val applicationInfo = context.packageManager.getApplicationInfo(
                context.packageName, PackageManager.GET_META_DATA
            )
            val bundle = applicationInfo.metaData
                ?: throw IllegalStateException("No metadata found in manifest")
            val apiKey = bundle.getString("com.google.android.geo.API_KEY")
                ?: throw IllegalStateException("No API key found in manifest metadata")

            if (apiKey.isEmpty()) {
                throw IllegalStateException("API key is empty")
            }
            return apiKey
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving API key: ${e.message}")
            throw IllegalArgumentException("Failed to get Maps API key: ${e.message}")
        }
    }

    /**
     * Refresh the autocomplete session token
     * Call this after completing a place selection
     */
    fun refreshSessionToken() {
        sessionToken = AutocompleteSessionToken.newInstance()
    }

    /**
     * Find place predictions based on query text
     * @param query The search query
     * @param onSuccess Callback for successful predictions
     * @param onError Callback for errors
     */
    fun findPlacePredictions(
        query: String,
        onSuccess: (List<AutocompletePrediction>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            // This is biased to UK, update as needed for your app's region focus
            .setLocationBias(
                RectangularBounds.newInstance(
                    LatLng(49.9, -7.6),  // Southwest corner of the UK
                    LatLng(60.9, 1.8)    // Northeast corner of the UK
                )
            )
            .setSessionToken(sessionToken)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response -> onSuccess(response.autocompletePredictions) }
            .addOnFailureListener { exception -> onError(exception) }
    }

    /**
     * Fetch detailed place information including coordinates
     * @param placeId The ID of the place to fetch
     * @return A Place object with latitude and longitude
     */
    suspend fun fetchPlaceDetails(placeId: String): Place = suspendCancellableCoroutine { continuation ->
        // Specify the fields to return
        val placeFields = listOf(
            com.google.android.libraries.places.api.model.Place.Field.ID,
            com.google.android.libraries.places.api.model.Place.Field.NAME,
            com.google.android.libraries.places.api.model.Place.Field.ADDRESS,
            com.google.android.libraries.places.api.model.Place.Field.LAT_LNG
        )

        val request = FetchPlaceRequest.builder(placeId, placeFields)
            .setSessionToken(sessionToken)
            .build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                val appPlace = Place(
                    id = place.id ?: "",
                    name = place.name ?: "",
                    address = place.address ?: "",
                    fullText = "${place.name}, ${place.address}",
                    latitude = place.latLng?.latitude,
                    longitude = place.latLng?.longitude
                )

                // Refresh token after a selection is completed
                refreshSessionToken()

                continuation.resume(appPlace)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching place details: ${exception.message}")
                continuation.resumeWithException(exception)
            }
    }

    companion object {
        private const val TAG = "PlacesManager"
    }
}