package com.chirilglance.androidglancedna.core.ui.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.chirilglance.androidglancedna.presentation.common.useActiveProfile
import com.chirilglance.androidglancedna.presentation.examples.profile.ProfileManagementViewModel
import com.chirilglance.androidglancedna.presentation.navigation.Screen

/**
 * Top app bar with navigation and profile features
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlanceTopAppBar(
    navController: NavController,
    currentRoute: String,
    viewModel: ProfileManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val activeProfile = useActiveProfile()

    var showProfileMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            // Title based on current route
            val title = when {
                currentRoute.startsWith(Screen.Home.route) -> "GlanceDNA"
                currentRoute.startsWith(Screen.UiComponents.route) -> "UI Components"
                currentRoute.startsWith(Screen.FormValidation.route) -> "Form Validation"
                currentRoute.startsWith(Screen.Authentication.route) -> "Authentication"
                currentRoute.startsWith(Screen.LocationServices.route) -> "Location Services"
                currentRoute.startsWith(Screen.ProfileManagement.route) -> "Profile Management"
                currentRoute.startsWith("profile_details") -> "Profile Details"
                currentRoute.startsWith("profile_edit") -> "Edit Profile"
                else -> "GlanceDNA"
            }
            Text(text = title)
        },
        navigationIcon = {
            // Only show back button if not on home screen
            if (currentRoute != Screen.Home.route && navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            // Profile icon/menu
            Box {
                // Profile icon that shows dropdown when clicked
                IconButton(onClick = { showProfileMenu = true }) {
                    if (activeProfile != null && !activeProfile.imageUrl.isNullOrEmpty()) {
                        // Display profile image if available
                        AsyncImage(
                            model = activeProfile.imageUrl,
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        // Default profile icon
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    }
                }

                // Profile dropdown menu
                DropdownMenu(
                    expanded = showProfileMenu,
                    onDismissRequest = { showProfileMenu = false }
                ) {
                    // Active profile info
                    activeProfile?.let { profile ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!profile.imageUrl.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = profile.imageUrl,
                                            contentDescription = "Profile",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Text(
                                        text = profile.displayName,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            },
                            onClick = {
                                showProfileMenu = false
                                navController.navigate(Screen.ProfileDetails.createRoute(profile.profileId))
                            }
                        )
                    }

                    // Profile management
                    DropdownMenuItem(
                        text = { Text("Manage Profiles") },
                        onClick = {
                            showProfileMenu = false
                            navController.navigate(Screen.ProfileManagement.route)
                        }
                    )

                    // Settings
                    DropdownMenuItem(
                        text = { Text("Settings") },
                        onClick = {
                            showProfileMenu = false
                            // In a real app, navigate to settings screen
                        }
                    )
                }
            }

            // Settings icon
            IconButton(
                onClick = { /* Navigate to settings in a real app */ }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    )
}