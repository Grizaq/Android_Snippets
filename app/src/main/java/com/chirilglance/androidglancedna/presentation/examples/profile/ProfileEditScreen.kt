package com.chirilglance.androidglancedna.presentation.examples.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.AutoErrorHandler

/**
 * Note: This is a simplified edit screen for demonstration purposes.
 * In a real application, you would implement proper form validation and
 * editing functionality. For this demo, we just show the profile info
 * and explain what would be editable.
 */
@Composable
fun ProfileEditScreen(
    profileId: String,
    navController: NavController,
    viewModel: ProfileManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AutoErrorHandler(
        state = uiState
    )

    var showUnsavedChangesDialog by remember { mutableStateOf(false) }

    // Main content
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (uiState) {
            is UiState.Loading -> {
                LoadingState()
            }

            is UiState.Empty -> {
                EmptyState(navController)
            }

            is UiState.Error -> {
                ErrorState(errorMessage = (uiState as UiState.Error).message,
                    onRetry = { viewModel.loadProfiles() },
                    onBack = { navController.popBackStack() })
            }

            is UiState.Success -> {
                val profileData = (uiState as UiState.Success<ProfileData>).data
                val profile = profileData.profiles.find { it.profileId == profileId }

                if (profile == null) {
                    ProfileNotFoundState(navController)
                } else {
                    // Profile edit form
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Edit Profile Information",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Profile image at the top (would be editable in a real app)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ProfileImage(profile = profile, size = 120.dp)
                        }

                        // Form fields (simplified for demo)
                        OutlinedTextField(
                            value = profile.name,
                            onValueChange = { /* Would update in real app */ },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = profile.displayName,
                            onValueChange = { /* Would update in real app */ },
                            label = { Text("Display Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = profile.email,
                            onValueChange = { /* Would update in real app */ },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = profile.phoneNumber ?: "",
                            onValueChange = { /* Would update in real app */ },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (profile.hasTeam()) {
                            OutlinedTextField(
                                value = profile.teamName ?: "",
                                onValueChange = { /* Would update in real app */ },
                                label = { Text("Team Name") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Demo note
                        Text(
                            text = "Note: This is a simplified demo. In a real app, these fields would be editable and changes would be saved to the secure storage.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Save button
                        Button(
                            onClick = {
                                // In a real app, save changes and navigate back
                                navController.popBackStack()
                            }, modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Changes")
                        }
                    }
                }
            }
        }
    }

    // Unsaved changes dialog
    if (showUnsavedChangesDialog) {
        AlertDialog(onDismissRequest = { showUnsavedChangesDialog = false },
            title = { Text("Discard Changes?") },
            text = { Text("You have unsaved changes. Are you sure you want to discard them?") },
            confirmButton = {
                TextButton(onClick = {
                    showUnsavedChangesDialog = false
                    navController.popBackStack()
                }) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnsavedChangesDialog = false }) {
                    Text("Cancel")
                }
            })
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading profile...")
        }
    }
}

@Composable
private fun EmptyState(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No profiles available", style = MaterialTheme.typography.titleMedium
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
    errorMessage: String, onRetry: () -> Unit, onBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Error loading profile", style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onBack) {
                Text("Go Back")
            }
        }
    }
}

@Composable
private fun ProfileNotFoundState(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profile not found", style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Go Back")
            }
        }
    }
}