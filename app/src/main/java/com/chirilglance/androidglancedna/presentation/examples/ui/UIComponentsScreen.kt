package com.chirilglance.androidglancedna.presentation.examples.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.AutoErrorHandler
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectCard
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectCardDefaults
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectTextField
import com.chirilglance.androidglancedna.core.ui.components.LabeledTextField
import com.chirilglance.androidglancedna.core.ui.components.NumberTextField
import com.chirilglance.androidglancedna.core.ui.components.buttons.PrimaryButton
import com.chirilglance.androidglancedna.core.ui.components.buttons.SecondaryButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UIComponentsScreen(
    viewModel: UIComponentsViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
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
        // Snackbar Demonstrations
        ComponentSection(title = "Snackbar Demos") {
            Text(
                text = "Snackbars provide brief messages about app processes with optional actions.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Basic Snackbar types
            Text(
                text = "Basic Snackbar Types",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
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

            Spacer(modifier = Modifier.height(16.dp))

            // Advanced Snackbars
            Text(
                text = "Advanced Snackbars",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
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

            Spacer(modifier = Modifier.height(16.dp))

            // UiState Integration
            Text(
                text = "UiState Integration",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Status display
            ClubConnectCard(modifier = Modifier.fillMaxWidth(),
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
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
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
        // Buttons Section
        ComponentSection(title = "Buttons") {
            // Primary Button
            PrimaryButton(
                text = "Primary Button",
                onClick = { /* Handle click */ },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Primary Button with loading state
            var isLoading by remember { mutableStateOf(false) }
            PrimaryButton(
                text = "Loading Button", onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        delay(2000)
                        isLoading = false
                    }
                }, isLoading = isLoading, modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Custom colored button
            PrimaryButton(
                text = "Custom Color Button",
                onClick = { /* Handle click */ },
                containerColor = Color(0xFF3F51B5), // Indigo
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary Button
            SecondaryButton(
                text = "Secondary Button",
                onClick = { /* Handle click */ },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Custom colored secondary button
            SecondaryButton(
                text = "Custom Secondary Button",
                onClick = { /* Handle click */ },
                contentColor = Color(0xFF3F51B5), // Indigo
                borderColor = Color(0xFF3F51B5), // Indigo
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Disabled Button
            PrimaryButton(
                text = "Disabled Button",
                onClick = { /* Handle click */ },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Text Fields Section
        ComponentSection(title = "Text Fields") {
            // Basic Text Field
            var text by remember { mutableStateOf("") }
            ClubConnectTextField(
                value = text,
                onValueChange = { text = it },
                label = "Basic Text Field",
                hint = "Enter text here",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Text Field with Error
            var emailText by remember { mutableStateOf("") }
            var emailError by remember { mutableStateOf<String?>(null) }
            ClubConnectTextField(
                value = emailText,
                onValueChange = {
                    emailText = it
                    emailError = if (!it.contains("@") && it.isNotEmpty()) {
                        "Please enter a valid email"
                    } else {
                        null
                    }
                },
                label = "Email",
                hint = "Enter your email",
                errorMessage = emailError,
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Number Text Field
            var numberText by remember { mutableStateOf("") }
            NumberTextField(
                value = numberText,
                onValueChange = { numberText = it },
                label = "Integer Input",
                hint = "Enter a number",
                isInteger = true,
                minValue = 0f,
                maxValue = 100f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Decimal Number Text Field
            var decimalText by remember { mutableStateOf("") }
            NumberTextField(
                value = decimalText,
                onValueChange = { decimalText = it },
                label = "Decimal Input",
                hint = "Enter a decimal number",
                isInteger = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Enhanced LabeledTextField inside Card
            var labeledCardText by remember { mutableStateOf("") }
            LabeledTextField(
                value = labeledCardText,
                onValueChange = { labeledCardText = it },
                label = "Username:",
                hint = "Enter username...",
                elevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Text Field with Icon
            var searchText by remember { mutableStateOf("") }
            ClubConnectTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = "Search",
                hint = "Search for items",
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search, contentDescription = "Search"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Cards Section
        ComponentSection(title = "Cards") {
            // Basic Card
            ClubConnectCard(
                titleContent = ClubConnectCardDefaults.Title("Card Title"),
                descriptionContent = ClubConnectCardDefaults.Description("This is a basic card with title and description."),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card with Icon
            ClubConnectCard(
                titleContent = ClubConnectCardDefaults.Title("Card with Icon"),
                descriptionContent = ClubConnectCardDefaults.Description("This card includes an icon in the top left."),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Clickable Card
            var clickCount by remember { mutableIntStateOf(0) }
            ClubConnectCard(
                titleContent = ClubConnectCardDefaults.Title("Clickable Card"),
                descriptionContent = {
                    Text(
                        text = if (clickCount == 0) "Click me!" else "Clicked $clickCount times",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                },
                onClick = { clickCount++ },
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Colored Card
            ClubConnectCard(titleContent = {
                Text(
                    text = "Colored Card",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF2E7D32) // Dark green
                )
            }, descriptionContent = {
                Text(
                    text = "This card has custom background and text colors.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF2E7D32).copy(alpha = 0.7f) // Dark green with alpha
                )
            }, containerColor = Color(0xFFE8F5E9), // Light green
                contentColor = Color(0xFF2E7D32), // Dark green
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card with Horizontally-arranged Action Buttons
            ClubConnectCard(
                titleContent = ClubConnectCardDefaults.Title("Card with Actions"),
                subtitleContent = ClubConnectCardDefaults.Subtitle("With Horizontal Buttons"),
                descriptionContent = ClubConnectCardDefaults.Description("This card includes action buttons arranged horizontally."),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = Color(0xFFE57373) // Light red
                    )
                },
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { /* Handle click */ }) {
                            Text(text = "Cancel", color = MaterialTheme.colorScheme.secondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { /* Handle click */ }) {
                            Text("Confirm")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card with Vertically Stacked Buttons
            ClubConnectCard(
                titleContent = ClubConnectCardDefaults.Title("Card with Stacked Buttons"),
                descriptionContent = ClubConnectCardDefaults.Description("This card has buttons stacked vertically with padding."),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                },
                actions = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        PrimaryButton(
                            text = "Primary Action",
                            onClick = { /* Handle click */ },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SecondaryButton(
                            text = "Secondary Action",
                            onClick = { /* Handle click */ },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}

@Composable
private fun ComponentSection(
    title: String, content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        content()
    }
}