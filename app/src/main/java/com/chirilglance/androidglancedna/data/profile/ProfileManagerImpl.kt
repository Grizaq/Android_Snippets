package com.chirilglance.androidglancedna.data.profile

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.chirilglance.androidglancedna.core.data.storage.SecureStorageManager
import com.chirilglance.androidglancedna.domain.models.profile.UserProfile
import com.chirilglance.androidglancedna.domain.models.profile.UserRoleType
import com.chirilglance.androidglancedna.domain.profile.ProfileManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the ProfileManager interface that uses secure encrypted storage.
 */
@Singleton
class ProfileManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val secureStorageManager: SecureStorageManager
) : ProfileManager {

    // Create the encrypted shared preferences
    private val sharedPreferences: SharedPreferences by lazy {
        secureStorageManager.createEncryptedPreferences("encrypted_profile_preferences")
    }

    // JSON parser for serialization/deserialization
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = false
    }

    companion object {
        private const val KEY_ACTIVE_PROFILE = "active_profile"
        private const val KEY_ALL_PROFILES = "all_profiles"
        private const val KEY_PREFERRED_ROLE_TYPE = "preferred_role_type"
        private const val KEY_PREFERRED_PROFILE_ID = "preferred_profile_id"
        private const val KEY_DEFAULT_TEAM_PREFIX = "default_team_"
        private const val KEY_CUSTOM_ATTRIBUTE_PREFIX = "attr_"
        private const val TAG = "ProfileManager"
    }

    override suspend fun saveActiveProfile(profile: UserProfile) {
        withContext(Dispatchers.IO) {
            try {

                Log.d(TAG, "Starting saveActiveProfile for: ${profile.profileId}")
                val profileJson = json.encodeToString(profile)
                Log.d(TAG, "Profile JSON to save: $profileJson")

                // Use commit() instead of apply() to ensure immediate write
                val success = sharedPreferences.edit().putString(KEY_ACTIVE_PROFILE, profileJson)
                    .putString(KEY_PREFERRED_ROLE_TYPE, profile.roleType.name)
                    .putString(KEY_PREFERRED_PROFILE_ID, profile.profileId).commit()

                if (success) {
                    Log.d(TAG, "Successfully committed active profile to SharedPreferences")
                    Log.i(TAG, "Active profile saved: ${profile.roleType} (${profile.displayName})")
                } else {
                    Log.e(TAG, "Failed to save active profile")
                }
                addProfile(profile)

                // Verify the profile was saved
                val verifyProfileJson = sharedPreferences.getString(KEY_ACTIVE_PROFILE, null)
                Log.d(TAG, "Verification - saved profile JSON: $verifyProfileJson")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving active profile", e)
            }
        }
    }

    override suspend fun getActiveProfile(): UserProfile? {
        return withContext(Dispatchers.IO) {
            val profileJson = secureStorageManager.safeStringRead(
                sharedPreferences, KEY_ACTIVE_PROFILE
            ) ?: return@withContext null

            try {
                val profile = json.decodeFromString<UserProfile>(profileJson)
                Log.i(TAG, "Retrieved active profile: ${profile.roleType} (${profile.displayName})")
                profile
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing active profile", e)
                null
            }
        }
    }

    override suspend fun addProfile(profile: UserProfile) {
        withContext(Dispatchers.IO) {
            try {
                val currentProfiles = getAllProfiles().toMutableList()

                // Check if profile with same ID already exists and replace it
                val existingIndex =
                    currentProfiles.indexOfFirst { it.profileId == profile.profileId }
                if (existingIndex >= 0) {
                    currentProfiles[existingIndex] = profile
                    Log.i(
                        TAG,
                        "Updated existing profile: ${profile.roleType} (${profile.displayName})"
                    )
                } else {
                    currentProfiles.add(profile)
                    Log.i(TAG, "Added new profile: ${profile.roleType} (${profile.displayName})")
                }

                // Save updated list
                val profilesJson = json.encodeToString(currentProfiles)
                secureStorageManager.safeStringWrite(
                    sharedPreferences,
                    KEY_ALL_PROFILES,
                    profilesJson
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error adding profile", e)
            }
        }
    }

    override suspend fun getAllProfiles(): List<UserProfile> {
        return withContext(Dispatchers.IO) {
            val profilesJson = secureStorageManager.safeStringRead(
                sharedPreferences, KEY_ALL_PROFILES, "[]"
            )

            try {
                val profiles = json.decodeFromString<List<UserProfile>>(profilesJson ?: "[]")
                Log.i(TAG, "Retrieved ${profiles.size} profiles")
                profiles
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing profiles list", e)
                emptyList()
            }
        }
    }

    override fun hasProfiles(): Boolean {
        val hasProfiles = try {
            val profilesJson = sharedPreferences.getString(KEY_ALL_PROFILES, "[]")
            !(profilesJson.isNullOrEmpty() || profilesJson == "[]")
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if has profiles", e)
            false
        }

        Log.i(TAG, "User has profiles: $hasProfiles")
        return hasProfiles
    }

    override suspend fun clearProfiles() {
        withContext(Dispatchers.IO) {
            try {
                val editor =
                    sharedPreferences.edit().remove(KEY_ACTIVE_PROFILE).remove(KEY_ALL_PROFILES)
                        .remove(KEY_PREFERRED_ROLE_TYPE).remove(KEY_PREFERRED_PROFILE_ID)

                // Clear default team preferences for all role types too
                val keySet = sharedPreferences.all.keys
                keySet.filter { it.startsWith(KEY_DEFAULT_TEAM_PREFIX) }.forEach { key ->
                    editor.remove(key)
                }

                // Clear custom attributes
                keySet.filter { it.startsWith(KEY_CUSTOM_ATTRIBUTE_PREFIX) }.forEach { key ->
                    editor.remove(key)
                }

                val result = editor.commit() // Use commit instead of apply for immediate effect

                if (result) {
                    Log.i(TAG, "All profile data cleared successfully (including preferences)")
                } else {
                    Log.w(TAG, "Failed to clear profile data")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing profiles", e)
            }
        }
    }

    override suspend fun clearProfilesPreservingActivePreference() {
        withContext(Dispatchers.IO) {
            try {
                // Get current active profile before clearing
                val currentActiveProfile = getActiveProfile()

                // Clear profiles data but preserve preference
                sharedPreferences.edit().remove(KEY_ACTIVE_PROFILE).remove(KEY_ALL_PROFILES).apply()

                // Log what we're preserving
                if (currentActiveProfile != null) {
                    Log.i(
                        TAG,
                        "Cleared profiles but preserved preference for: ${currentActiveProfile.roleType} (${currentActiveProfile.profileId})"
                    )
                } else {
                    Log.i(TAG, "Cleared profiles, no active profile preference to preserve")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing profiles while preserving preference", e)
            }
        }
    }

    override suspend fun syncProfilesWithPreferenceRestore(freshProfiles: List<UserProfile>): UserProfile? {
        return withContext(Dispatchers.IO) {
            try {
                if (freshProfiles.isEmpty()) {
                    Log.i(TAG, "No fresh profiles to sync")
                    return@withContext null
                }

                // Save all fresh profiles
                val profilesJson = json.encodeToString(freshProfiles)
                secureStorageManager.safeStringWrite(
                    sharedPreferences,
                    KEY_ALL_PROFILES,
                    profilesJson
                )
                Log.i(TAG, "Synced ${freshProfiles.size} fresh profiles")

                // Try to restore previous active profile preference
                val preferredRoleType = secureStorageManager.safeStringRead(
                    sharedPreferences, KEY_PREFERRED_ROLE_TYPE
                )
                val preferredProfileId = secureStorageManager.safeStringRead(
                    sharedPreferences, KEY_PREFERRED_PROFILE_ID
                )

                var profileToActivate: UserProfile? = null

                if (!preferredProfileId.isNullOrEmpty()) {
                    // Try to find the exact profile by ID first
                    profileToActivate = freshProfiles.find { it.profileId == preferredProfileId }

                    if (profileToActivate != null) {
                        Log.i(
                            TAG,
                            "Restored exact preferred profile: ${profileToActivate.roleType} (${profileToActivate.displayName})"
                        )
                    } else if (!preferredRoleType.isNullOrEmpty()) {
                        // If exact profile not found, try to find same role type
                        try {
                            val preferredType = UserRoleType.fromString(preferredRoleType)
                            profileToActivate = freshProfiles.find { it.roleType == preferredType }

                            if (profileToActivate != null) {
                                Log.i(
                                    TAG,
                                    "Restored profile by type: ${profileToActivate.roleType} (${profileToActivate.displayName})"
                                )
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Invalid preferred role type: $preferredRoleType")
                        }
                    }
                }

                // Fallback to first profile if no preference could be restored
                if (profileToActivate == null && freshProfiles.isNotEmpty()) {
                    profileToActivate = freshProfiles.first()
                    Log.i(
                        TAG,
                        "No preference restored, using first profile: ${profileToActivate.roleType} (${profileToActivate.displayName})"
                    )
                }

                // Set the determined profile as active
                profileToActivate?.let { saveActiveProfile(it) }
                return@withContext profileToActivate
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing profiles with preference restore", e)
                null
            }
        }
    }

    override suspend fun removeProfile(profileId: String) {
        withContext(Dispatchers.IO) {
            try {
                val currentProfiles = getAllProfiles().toMutableList()
                val previousSize = currentProfiles.size

                currentProfiles.removeAll { it.profileId == profileId }

                if (currentProfiles.size < previousSize) {
                    // Profile was removed
                    val profilesJson = json.encodeToString(currentProfiles)
                    secureStorageManager.safeStringWrite(
                        sharedPreferences,
                        KEY_ALL_PROFILES,
                        profilesJson
                    )
                    Log.i(TAG, "Removed profile with ID: $profileId")

                    // If active profile was removed, update it
                    val activeProfile = getActiveProfile()
                    if (activeProfile?.profileId == profileId) {
                        if (currentProfiles.isNotEmpty()) {
                            saveActiveProfile(currentProfiles.first())
                        } else {
                            secureStorageManager.safeRemoveKey(
                                sharedPreferences,
                                KEY_ACTIVE_PROFILE
                            )
                        }
                    } else {
                    }
                } else {
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error removing profile", e)
            }
        }
    }

    override suspend fun updateProfileTeam(
        profileId: String, teamId: String, teamName: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Get all profiles
                val currentProfiles = getAllProfiles().toMutableList()

                // Find the profile to update
                val profileIndex = currentProfiles.indexOfFirst { it.profileId == profileId }
                if (profileIndex < 0) {
                    Log.e(TAG, "Profile with ID $profileId not found")
                    return@withContext false
                }

                // Update the profile with team info
                val originalProfile = currentProfiles[profileIndex]
                val updatedProfile = originalProfile.copy(
                    teamId = teamId, teamName = teamName
                )

                // Replace in the list
                currentProfiles[profileIndex] = updatedProfile

                // Save updated list
                val profilesJson = json.encodeToString(currentProfiles)
                secureStorageManager.safeStringWrite(
                    sharedPreferences,
                    KEY_ALL_PROFILES,
                    profilesJson
                )

                // If this is the active profile, update that too
                val activeProfile = getActiveProfile()
                if (activeProfile?.profileId == profileId) {
                    saveActiveProfile(updatedProfile)
                }

                Log.i(
                    TAG,
                    "Updated team for profile ${updatedProfile.roleType} (${updatedProfile.displayName}): teamId=$teamId, teamName=$teamName"
                )
                return@withContext true
            } catch (e: Exception) {
                Log.e(TAG, "Error updating profile team", e)
                return@withContext false
            }
        }
    }

    override suspend fun saveDefaultTeamForRoleType(
        roleType: UserRoleType, teamId: String, teamName: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Create a map for the team data
                val teamMap = mapOf(
                    "id" to teamId, "name" to teamName
                )

                // Convert to JSON string
                val teamJson = json.encodeToString(teamMap)

                // Save using the role type as part of the key
                secureStorageManager.safeStringWrite(
                    sharedPreferences, "${KEY_DEFAULT_TEAM_PREFIX}${roleType.name}", teamJson
                )

                Log.i(TAG, "Saved default team for role type ${roleType.name}: $teamName")
                return@withContext true
            } catch (e: Exception) {
                Log.e(TAG, "Error saving default team for role type ${roleType.name}", e)
                return@withContext false
            }
        }
    }

    override suspend fun getDefaultTeamForRoleType(roleType: UserRoleType): Pair<String, String>? {
        return withContext(Dispatchers.IO) {
            try {
                val teamJson = secureStorageManager.safeStringRead(
                    sharedPreferences, "${KEY_DEFAULT_TEAM_PREFIX}${roleType.name}"
                ) ?: return@withContext null

                // Parse the saved team info as a Map
                val teamMap = json.decodeFromString<Map<String, String>>(teamJson)

                val id = teamMap["id"] ?: return@withContext null
                val name = teamMap["name"] ?: return@withContext null

                return@withContext Pair(id, name)
            } catch (e: Exception) {
                Log.e(TAG, "Error getting default team for role type ${roleType.name}", e)
                return@withContext null
            }
        }
    }

    override suspend fun updateProfileAttribute(
        profileId: String, key: String, value: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Get all profiles
                val currentProfiles = getAllProfiles().toMutableList()

                // Find the profile to update
                val profileIndex = currentProfiles.indexOfFirst { it.profileId == profileId }
                if (profileIndex < 0) {
                    Log.e(TAG, "Profile with ID $profileId not found for attribute update")
                    return@withContext false
                }

                // Update the profile with custom attribute
                val originalProfile = currentProfiles[profileIndex]
                val updatedAttributes = originalProfile.customAttributes.toMutableMap().apply {
                    put(key, value)
                }
                val updatedProfile = originalProfile.copy(
                    customAttributes = updatedAttributes
                )

                // Replace in the list
                currentProfiles[profileIndex] = updatedProfile

                // Save updated list
                val profilesJson = json.encodeToString(currentProfiles)
                secureStorageManager.safeStringWrite(
                    sharedPreferences,
                    KEY_ALL_PROFILES,
                    profilesJson
                )

                // If this is the active profile, update that too
                val activeProfile = getActiveProfile()
                if (activeProfile?.profileId == profileId) {
                    saveActiveProfile(updatedProfile)
                }

                // Also save attribute separately for faster lookup
                secureStorageManager.safeStringWrite(
                    sharedPreferences, "${KEY_CUSTOM_ATTRIBUTE_PREFIX}${profileId}_$key", value
                )

                Log.i(TAG, "Updated attribute $key for profile $profileId")
                return@withContext true
            } catch (e: Exception) {
                Log.e(TAG, "Error updating profile attribute", e)
                return@withContext false
            }
        }
    }

    override suspend fun getProfileAttribute(profileId: String, key: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                // First try to get from dedicated storage (faster)
                val fastValue = secureStorageManager.safeStringRead(
                    sharedPreferences, "${KEY_CUSTOM_ATTRIBUTE_PREFIX}${profileId}_$key"
                )

                if (!fastValue.isNullOrEmpty()) {
                    return@withContext fastValue
                }

                // If not found, try to get from profile object
                val profile = getAllProfiles().find { it.profileId == profileId }
                return@withContext profile?.customAttributes?.get(key)
            } catch (e: Exception) {
                Log.e(TAG, "Error getting profile attribute $key for profile $profileId", e)
                null
            }
        }
    }

    override suspend fun refreshProfiles() {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Starting profile refresh...")
                // Read from encrypted shared preferences directly
                val profilesJson = secureStorageManager.safeStringRead(
                    sharedPreferences, KEY_ALL_PROFILES, "[]"
                )
                val activeProfileJson = secureStorageManager.safeStringRead(
                    sharedPreferences, KEY_ACTIVE_PROFILE
                )
                Log.d(TAG, "Raw active profile JSON: $activeProfileJson")

                // Log for debugging
                Log.d(TAG, "Refreshing profiles from storage")

                if (!profilesJson.isNullOrEmpty() && profilesJson != "[]") {
                    try {
                        // Parse the profiles list
                        val profiles = json.decodeFromString<List<UserProfile>>(profilesJson)
                        Log.d(TAG, "Refreshed ${profiles.size} profiles from storage")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing profiles during refresh", e)
                    }
                } else {
                    Log.d(TAG, "No profiles found in storage during refresh")
                }

                if (!activeProfileJson.isNullOrEmpty()) {
                    try {
                        // Parse the active profile
                        val activeProfile = json.decodeFromString<UserProfile>(activeProfileJson)
                        Log.d(
                            TAG,
                            "Refreshed active profile: ${activeProfile.roleType} (${activeProfile.displayName})"
                        )

                        // Log team info for debugging
                        if (activeProfile.hasTeam()) {
                            Log.d(
                                TAG,
                                "Active profile has team: ${activeProfile.teamName} (${activeProfile.teamId})"
                            )
                        } else {
                            Log.d(TAG, "Active profile has no team")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing active profile during refresh", e)
                    }
                } else {
                    Log.d(TAG, "No active profile found in storage during refresh")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing profiles", e)
            }
        }
    }
}