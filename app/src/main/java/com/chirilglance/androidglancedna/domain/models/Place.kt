package com.chirilglance.androidglancedna.domain.models

/**
 * Data class representing a location place with optional coordinates
 */
data class Place(
    val id: String,
    val name: String,
    val address: String,
    val fullText: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)