package com.chirilglance.androidglancedna.data.repository

import android.util.Log
import com.chirilglance.androidglancedna.domain.models.Place
import com.chirilglance.androidglancedna.domain.repository.PlacesRepository
import com.chirilglance.androidglancedna.domain.services.PlacesManager
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of PlacesRepository that uses Google Places API
 */
@Singleton
class PlacesRepositoryImpl @Inject constructor(
    private val placesManager: PlacesManager
) : PlacesRepository {

    override fun searchPlaces(query: String): Flow<Result<List<Place>>> = callbackFlow {
        // Early return for short queries
        if (query.length < 2) {
            trySend(Result.success(emptyList()))
            return@callbackFlow
        }

        placesManager.findPlacePredictions(
            query = query,
            onSuccess = { predictions ->
                val places = predictions.map { it.toPlace() }
                trySend(Result.success(places))
            },
            onError = { exception ->
                Log.e(TAG, "Error searching places", exception)
                trySend(Result.failure(exception))
            }
        )

        // No cleanup needed
        awaitClose { }
    }

    override suspend fun getPlaceDetails(placeId: String): Result<Place> {
        return try {
            val place = placesManager.fetchPlaceDetails(placeId)
            Result.success(place)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching place details", e)
            Result.failure(e)
        }
    }

    /**
     * Convert Google AutocompletePrediction to our Place model
     */
    private fun AutocompletePrediction.toPlace(): Place = Place(
        id = placeId,
        name = getPrimaryText(null).toString(),
        address = getSecondaryText(null).toString(),
        fullText = getFullText(null).toString()
        // Coordinates will be fetched when a place is selected
    )

    companion object {
        private const val TAG = "PlacesRepository"
    }
}