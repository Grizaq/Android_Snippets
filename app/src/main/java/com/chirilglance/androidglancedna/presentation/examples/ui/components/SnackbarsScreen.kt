package com.chirilglance.androidglancedna.presentation.examples.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.AutoErrorHandler
import com.chirilglance.androidglancedna.core.ui.components.DefaultCard
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectCardDefaults
import com.chirilglance.androidglancedna.core.ui.components.buttons.PrimaryButton
import com.chirilglance.androidglancedna.core.ui.components.buttons.SecondaryButton
import com.chirilglance.androidglancedna.presentation.examples.ui.ComponentSection
import com.chirilglance.androidglancedna.presentation.examples.ui.UIComponentsViewModel

@Composable
fun SnackbarsScreen(
    viewModel: UIComponentsViewModel = hiltViewModel()
) {
    val demoState by viewModel.demoState.collectAsState()

    // Handle errors automatically using AutoErrorHandler
    AutoErrorHandler(state = demoState, onRetry = { viewModel.simulateLoading() })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Snackbar Examples", style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Snackbars provide brief messages about app processes at the bottom of the screen.",
            style = MaterialTheme.typography.bodyLarge
        )

        // Basic Snackbar Types
        ComponentSection(title = "Basic Snackbar Types") {
            Text(
                text = "Simple snackbars for different message types.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PrimaryButton(
                    text = "Error",
                    onClick = { viewModel.showBasicErrorSnackbar() },
                    modifier = Modifier.weight(1f)
                )

                PrimaryButton(
                    text = "Success",
                    onClick = { viewModel.showBasicSuccessSnackbar() },
                    modifier = Modifier.weight(1f)
                )

                PrimaryButton(
                    text = "Info",
                    onClick = { viewModel.showBasicInfoSnackbar() },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Advanced Snackbars
        ComponentSection(title = "Advanced Snackbars") {
            Text(
                text = "Snackbars with additional features like actions and custom durations.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SecondaryButton(
                    text = "With Action",
                    onClick = { viewModel.showErrorWithAction() },
                    modifier = Modifier.weight(1f)
                )

                SecondaryButton(
                    text = "Long Duration",
                    onClick = { viewModel.showLongDurationSnackbar() },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SecondaryButton(
                text = "Indefinite (Manual Close)",
                onClick = { viewModel.showIndefiniteSnackbar() },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // UiState Integration
        ComponentSection(title = "UiState Integration") {
            Text(
                text = "Snackbars can be automatically shown based on state changes.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Status display
            DefaultCard(modifier = Modifier.fillMaxWidth(),
                titleContent = ClubConnectCardDefaults.Title("Demo State"),
                descriptionContent = {
                    when (demoState) {
                        is UiState.Empty -> {
                            Text(
                                text = "Idle - Press a button below to start",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        is UiState.Loading -> {
                            Row(
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                androidx.compose.material3.CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Loading...", style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        is UiState.Success -> {
                            Text(
                                text = "Success: ${(demoState as UiState.Success<String>).data}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        is UiState.Error -> {
                            Text(
                                text = "Error state - check Snackbar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                })

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PrimaryButton(
                    text = "Simulate Success",
                    onClick = { viewModel.simulateLoading() },
                    enabled = demoState !is UiState.Loading,
                    modifier = Modifier.weight(1f)
                )

                PrimaryButton(
                    text = "Simulate Error",
                    onClick = { viewModel.simulateError() },
                    enabled = demoState !is UiState.Loading,
                    modifier = Modifier.weight(1f)
                )
            }

            SecondaryButton(
                text = "Reset Demo",
                onClick = { viewModel.resetDemoState() },
                enabled = demoState !is UiState.Empty,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}