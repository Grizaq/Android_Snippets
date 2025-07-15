package com.chirilglance.androidglancedna.domain.profile

import com.chirilglance.androidglancedna.domain.models.profile.UserProfile
import com.chirilglance.androidglancedna.domain.models.profile.UserRoleType

/**
 * Interface defining the profile management operations.
 * This interface abstracts the operations for managing user profiles
 * and allows for multiple implementation strategies.
 */
interface ProfileManager {
    /**
     * Save a profile as the active/selected profile
     * @param profile The profile to save as active
     */
    suspend fun saveActiveProfile(profile: UserProfile)

    /**
     * Get the currently active profile
     * @return The active profile or null if none is set
     */
    suspend fun getActiveProfile(): UserProfile?

    /**
     * Add a profile to the user's list of profiles
     * @param profile The profile to add
     */
    suspend fun addProfile(profile: UserProfile)

    /**
     * Get all profiles for the current user
     * @return List of all profiles or empty list if none
     */
    suspend fun getAllProfiles(): List<UserProfile>

    /**
     * Check if user has any profiles
     * @return true if at least one profile is saved
     */
    fun hasProfiles(): Boolean

    /**
     * Clear all profile data
     */
    suspend fun clearProfiles()

    /**
     * Clear profiles but preserve active profile preference for re-login scenarios
     * This stores the roleType and profileId of the current active profile before clearing
     */
    suspend fun clearProfilesPreservingActivePreference()

    /**
     * Sync fresh profiles from server while trying to restore previous active profile preference
     * @param freshProfiles The new profiles from server (or local creation)
     * @return The profile that was set as active (either restored preference or default)
     */
    suspend fun syncProfilesWithPreferenceRestore(freshProfiles: List<UserProfile>): UserProfile?

    /**
     * Remove a specific profile
     * @param profileId The ID of the profile to remove
     */
    suspend fun removeProfile(profileId: String)

    /**
     * Updates team information for a profile
     * @param profileId ID of the profile to update
     * @param teamId ID of the team to associate with the profile
     * @param teamName Name of the team
     * @return True if update was successful, false otherwise
     */
    suspend fun updateProfileTeam(
        profileId: String,
        teamId: String,
        teamName: String
    ): Boolean

    /**
     * Save default team for a role type
     */
    suspend fun saveDefaultTeamForRoleType(
        roleType: UserRoleType,
        teamId: String,
        teamName: String
    ): Boolean

    /**
     * Get default team for a role type
     * @return Pair of (teamId, teamName) or null if not found
     */
    suspend fun getDefaultTeamForRoleType(roleType: UserRoleType): Pair<String, String>?

    /**
     * Updates a custom attribute for a profile
     * @param profileId ID of the profile to update
     * @param key The attribute key
     * @param value The attribute value
     * @return True if update was successful, false otherwise
     */
    suspend fun updateProfileAttribute(
        profileId: String,
        key: String,
        value: String
    ): Boolean

    /**
     * Gets a custom attribute for a profile
     * @param profileId ID of the profile
     * @param key The attribute key
     * @return The attribute value or null if not found
     */
    suspend fun getProfileAttribute(
        profileId: String,
        key: String
    ): String?

    /**
     * Force refresh profiles from storage to ensure we have the latest data
     */
    suspend fun refreshProfiles()
}