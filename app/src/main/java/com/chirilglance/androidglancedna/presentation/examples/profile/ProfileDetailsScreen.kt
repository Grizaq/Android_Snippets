package com.chirilglance.androidglancedna.presentation.examples.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.AutoErrorHandler
import com.chirilglance.androidglancedna.domain.models.profile.UserProfile

/**
 * Screen for displaying profile details
 */
@Composable
fun ProfileDetailsScreen(
    profileId: String,
    navController: NavController,
    viewModel: ProfileManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AutoErrorHandler(
        state = uiState
    )

    // Load profiles if needed
    LaunchedEffect(Unit) {
        if (uiState !is UiState.Success) {
            viewModel.loadProfiles()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is UiState.Loading -> {
                LoadingState()
            }

            is UiState.Empty -> {
                EmptyState(navController)
            }

            is UiState.Error -> {
                ErrorState(
                    errorMessage = (uiState as UiState.Error).message,
                    onRetry = { viewModel.loadProfiles() },
                    onBack = { navController.popBackStack() }
                )
            }

            is UiState.Success -> {
                val profileData = (uiState as UiState.Success<ProfileData>).data
                val profile = profileData.profiles.find { it.profileId == profileId }

                if (profile == null) {
                    ProfileNotFoundState(navController)
                } else {
                    // Profile details content
                    ProfileDetails(
                        profile = profile,
                        isActive = profile.profileId == profileData.activeProfile?.profileId,
                        onSetActive = { viewModel.onProfileSelected(profile.profileId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading profiles...")
        }
    }
}

@Composable
private fun EmptyState(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No profiles available",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Go Back")
            }
        }
    }
}

@Composable
private fun ErrorState(
    errorMessage: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Error loading profiles",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onRetry) {
                    Text("Retry")
                }
                Button(onClick = onBack) {
                    Text("Go Back")
                }
            }
        }
    }
}

@Composable
private fun ProfileNotFoundState(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profile not found",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Go Back")
            }
        }
    }
}

/**
 * Composable for displaying profile details
 */
@Composable
fun ProfileDetails(
    profile: UserProfile, isActive: Boolean, onSetActive: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile image
        ProfileImage(
            profile = profile, size = 120.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Profile name
        Text(
            text = profile.displayName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Role
        Text(
            text = "Role: ${profile.roleType.name}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        // Active status
        if (isActive) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Active Profile",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onSetActive
            ) {
                Text("Set as Active Profile")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))

        // Contact information
        Text(
            text = "Contact Information",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Email
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Email", style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = profile.email, style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Phone (if available)
                profile.phoneNumber?.let { phone ->
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Phone", style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = phone, style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Team information (if available)
        if (profile.hasTeam()) {
            Text(
                text = "Team Information",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Team Name", style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = profile.teamName ?: "N/A",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Team ID", style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = profile.teamId ?: "N/A", style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Custom attributes (if any)
        if (profile.customAttributes.isNotEmpty()) {
            Text(
                text = "Additional Information",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    profile.customAttributes.forEach { (key, value) ->
                        Column {
                            Text(
                                text = key, style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = value, style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (key != profile.customAttributes.keys.last()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}