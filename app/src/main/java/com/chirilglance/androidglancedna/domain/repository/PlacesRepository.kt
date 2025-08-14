package com.chirilglance.androidglancedna.domain.repository

import com.chirilglance.androidglancedna.domain.models.Place
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Places API operations
 */
interface PlacesRepository {
    /**
     * Search for places based on a text query
     * @param query The search query text
     * @return Flow of Result containing a list of matching places
     */
    fun searchPlaces(query: String): Flow<Result<List<Place>>>

    /**
     * Get detailed information about a specific place including coordinates
     * @param placeId The Google Places ID of the place
     * @return Result containing the place details
     */
    suspend fun getPlaceDetails(placeId: String): Result<Place>
}