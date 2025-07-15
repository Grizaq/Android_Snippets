package com.chirilglance.androidglancedna.presentation.examples.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.AutoErrorHandler
import com.chirilglance.androidglancedna.domain.models.profile.UserProfile
import com.chirilglance.androidglancedna.presentation.navigation.Screen

/**
 * Screen for managing user profiles
 */
@Composable
fun ProfileManagementScreen(
    navController: NavController, viewModel: ProfileManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AutoErrorHandler(
        state = uiState
    )

    LaunchedEffect(Unit) {
        viewModel.loadProfiles()
    }

    // Extract active profile ID from current state
    val activeProfileId = when (uiState) {
        is UiState.Success -> (uiState as UiState.Success<ProfileData>).data.activeProfile?.profileId
        else -> null
    }

    var localSelectedProfileId by remember { mutableStateOf(activeProfileId) }

    // Update local state when active profile changes
    LaunchedEffect(activeProfileId) {
        localSelectedProfileId = activeProfileId
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Profile Management",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "This example demonstrates secure profile storage with role-based features. " + "Select a profile to set it as active.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Current active profile card
        if (uiState is UiState.Success) {
            val profileData = (uiState as UiState.Success<ProfileData>).data
            profileData.activeProfile?.let { activeProfile ->
                Text(
                    text = "Active Profile",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ActiveProfileCard(profile = activeProfile, onClick = {
                    navController.navigate(Screen.ProfileDetails.createRoute(activeProfile.profileId))
                })

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Profile list
        Text(
            text = "Available Profiles",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        when (uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {
                val errorMessage = (uiState as UiState.Error).message
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Error loading profiles: $errorMessage",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadProfiles() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh, contentDescription = "Retry"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Retry")
                        }
                    }
                }
            }

            is UiState.Success -> {
                val profileData = (uiState as UiState.Success<ProfileData>).data
                if (profileData.profiles.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No profiles found",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadProfiles() }) {
                                Text("Initialize Sample Profiles")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(profileData.profiles) { profile ->
                            val isLocallyActive = profile.profileId == localSelectedProfileId
                            ProfileListItem(profile = profile,
                                isActive = isLocallyActive,
                                onClick = {
                                    localSelectedProfileId = profile.profileId
                                    viewModel.onProfileSelected(profile.profileId)

                                    // Handle navigation separately
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Home.route) {
                                            inclusive = true
                                        }
                                    }
                                })
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            is UiState.Empty -> {
                // Show empty state UI
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No profiles available",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadProfiles() }) {
                            Text("Initialize Sample Profiles")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card displaying the active profile
 */
@Composable
fun ActiveProfileCard(
    profile: UserProfile, onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile image
            ProfileImage(profile = profile, size = 64.dp)

            Spacer(modifier = Modifier.width(16.dp))

            // Profile details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = profile.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Role: ${profile.roleType.name}",
                    style = MaterialTheme.typography.bodyMedium
                )

                profile.email.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (profile.hasTeam()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Team: ${profile.teamName}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Active indicator
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Active",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
            )
        }
    }
}

/**
 * List item for a profile
 */
@Composable
fun ProfileListItem(
    profile: UserProfile, isActive: Boolean, onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = if (isActive) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        // Wrap the content in a clickable surface to ensure clicks are registered
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    // Force disable any ripple effect that might be interfering
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick
                ), color = Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile image
                ProfileImage(
                    profile = profile, size = 48.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Profile details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = profile.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Role: ${profile.roleType.name}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Active indicator
                if (isActive) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Active",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Composable for displaying a profile image
 */
@Composable
fun ProfileImage(
    profile: UserProfile, size: androidx.compose.ui.unit.Dp
) {
    if (profile.imageUrl != null) {
        AsyncImage(
            model = profile.imageUrl,
            contentDescription = "Profile image for ${profile.displayName}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
        )
    } else {
        Surface(
            modifier = Modifier.size(size),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(size / 2)
                )
            }
        }
    }
}