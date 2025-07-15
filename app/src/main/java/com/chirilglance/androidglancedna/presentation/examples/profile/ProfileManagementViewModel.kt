package com.chirilglance.androidglancedna.presentation.examples.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.SnackbarManager
import com.chirilglance.androidglancedna.domain.models.profile.SampleProfiles
import com.chirilglance.androidglancedna.domain.models.profile.UserProfile
import com.chirilglance.androidglancedna.domain.profile.ProfileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Data class to hold profile information
 */
data class ProfileData(
    val profiles: List<UserProfile>,
    val activeProfile: UserProfile?
)

/**
 * ViewModel for the profile management screens
 */
@HiltViewModel
class ProfileManagementViewModel @Inject constructor(
    private val profileManager: ProfileManager
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow<UiState<ProfileData>>(UiState.Loading)
    val uiState: StateFlow<UiState<ProfileData>> = _uiState.asStateFlow()

    // Store current active profile ID for quicker access
    private var currentActiveProfileId: String? = null

    init {
        loadProfiles()
    }

    /**
     * Loads all profiles from the profile manager
     */
    fun loadProfiles() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val profiles = profileManager.getAllProfiles()
                val activeProfile = profileManager.getActiveProfile()

                // Update the cached active profile ID
                currentActiveProfileId = activeProfile?.profileId

                Log.d("ProfileViewModel", "Loaded active profile: ${activeProfile?.profileId}")

                if (profiles.isEmpty()) {
                    // First-time use: initialize with sample profiles
                    initializeSampleProfiles()
                } else {
                    val profileData = ProfileData(
                        profiles = profiles,
                        activeProfile = activeProfile
                    )

                    _uiState.value = UiState.Success(profileData)
                }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    Log.d("ProfileViewModel", "Profile loading job was cancelled")
                } else {
                    _uiState.value = UiState.Error(e.message ?: "Failed to load profiles")
                }
            }
        }
    }

    /**
     * Initializes the profile manager with sample profiles for demonstration
     */
    private suspend fun initializeSampleProfiles() {
        val sampleProfiles = SampleProfiles.getAllSampleProfiles()

        // Add sample profiles
        sampleProfiles.forEach { profile ->
            profileManager.addProfile(profile)
        }

        // Set the first profile as active
        val firstProfile = sampleProfiles.firstOrNull()
        firstProfile?.let {
            profileManager.saveActiveProfile(it)
            currentActiveProfileId = it.profileId
        }

        // Update UI state
        if (sampleProfiles.isEmpty()) {
            _uiState.value = UiState.Empty
        } else {
            val profileData = ProfileData(
                profiles = sampleProfiles,
                activeProfile = firstProfile
            )
            _uiState.value = UiState.Success(profileData)
        }
    }

    /**
     * Handles profile selection with immediate UI update and background storage
     *
     * @param profileId The ID of the profile to set as active
     */
    fun onProfileSelected(profileId: String) {
        // Don't do anything if it's already the active profile
        if (profileId == currentActiveProfileId) {
            return
        }

        // Get current state
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            val currentData = currentState.data
            val selectedProfile = currentData.profiles.find { it.profileId == profileId }

            // Update UI state immediately for better user experience
            if (selectedProfile != null) {
                currentActiveProfileId = profileId

                val updatedData = currentData.copy(activeProfile = selectedProfile)
                _uiState.value = UiState.Success(updatedData)

                // Launch a background job to update storage
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        // Save to storage
                        profileManager.saveActiveProfile(selectedProfile)

                        // After saving, verify it was saved correctly
                        val verifiedProfile = profileManager.getActiveProfile()

                        // If verification fails, log an error
                        if (verifiedProfile?.profileId != profileId) {
                            Log.e("ProfileViewModel", "Profile was not saved correctly. Expected: $profileId, Got: ${verifiedProfile?.profileId}")
                        } else {
                            Log.d("ProfileViewModel", "Profile saved and verified: $profileId")
                        }

                        // Show success message
                        SnackbarManager.showSuccess("Profile ${selectedProfile.displayName} is now active")
                    } catch (e: Exception) {
                        // Storage error doesn't affect UI since we've already updated it optimistically
                        if (e !is CancellationException) {
                            Log.e("ProfileViewModel", "Error saving profile", e)
                        }
                    }
                }
            }
        }
    }

    /**
     * Clears all profiles
     */
    fun clearAllProfiles() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                profileManager.clearProfiles()
                currentActiveProfileId = null
                _uiState.value = UiState.Empty
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to clear profiles")
            }
        }
    }

    /**
     * Gets a profile by ID
     *
     * @param profileId The ID of the profile to get
     * @return The profile or null if not found
     */
    fun getProfileById(profileId: String): UserProfile? {
        val currentState = _uiState.value
        return if (currentState is UiState.Success) {
            currentState.data.profiles.find { it.profileId == profileId }
        } else {
            null
        }
    }
}