package com.chirilglance.androidglancedna.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.domain.models.profile.UserProfile
import com.chirilglance.androidglancedna.presentation.examples.profile.ProfileData
import com.chirilglance.androidglancedna.presentation.examples.profile.ProfileManagementViewModel

/**
 * Hook to get the current active profile from anywhere in the app
 */
@Composable
fun useActiveProfile(): UserProfile? {
    val viewModel: ProfileManagementViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Only load profiles if not already loaded or loading
    LaunchedEffect(Unit) {
        if (uiState !is UiState.Success && uiState !is UiState.Loading) {
            viewModel.loadProfiles()
        }
    }

    // Extract active profile from state
    return when (uiState) {
        is UiState.Success -> {
            val profileData = (uiState as UiState.Success<ProfileData>).data
            profileData.activeProfile
        }
        else -> null
    }
}