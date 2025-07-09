package com.chirilglance.androidglancedna.examples.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectCard
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectTextField
import com.chirilglance.androidglancedna.core.ui.components.LabeledTextField
import com.chirilglance.androidglancedna.core.ui.components.NumberTextField
import com.chirilglance.androidglancedna.core.ui.components.PrimaryButton
import com.chirilglance.androidglancedna.core.ui.components.SecondaryButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UIComponentsScreen() {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "UI Components",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
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
                    text = "Loading Button",
                    onClick = {
                        isLoading = true
                        coroutineScope.launch {
                            delay(2000)
                            isLoading = false
                        }
                    },
                    isLoading = isLoading,
                    modifier = Modifier.fillMaxWidth()
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
                    hint = "Enter username",
                    elevation = 2.dp,
                    containerColor = Color(0xFFF8F8F8),
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
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Cards Section
            ComponentSection(title = "Cards") {
                // Basic Card
                ClubConnectCard(
                    title = "Card Title",
                    description = "This is a basic card with title and description.",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Card with Icon
                ClubConnectCard(
                    title = "Card with Icon",
                    description = "This card includes an icon in the top left.",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Clickable Card
                var clickCount by remember { mutableIntStateOf(0) }
                ClubConnectCard(
                    title = "Clickable Card",
                    description = if (clickCount == 0) "Click me!" else "Clicked $clickCount times",
                    onClick = { clickCount++ },
                    containerColor = Color(0xFFF5F5F5),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Colored Card
                ClubConnectCard(
                    title = "Colored Card",
                    description = "This card has custom background and text colors.",
                    containerColor = Color(0xFFE8F5E9), // Light green
                    contentColor = Color(0xFF2E7D32), // Dark green
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Card with Horizontally-arranged Action Buttons
                ClubConnectCard(
                    title = "Card with Actions",
                    subtitle = "With Horizontal Buttons",
                    description = "This card includes action buttons arranged horizontally.",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            tint = Color(0xFFE57373) // Light red
                        )
                    },
                    actions = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { /* Handle click */ }) {
                                Text("Cancel")
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
                    title = "Card with Stacked Buttons",
                    description = "This card has buttons stacked vertically with padding.",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    actions = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
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
}

@Composable
private fun ComponentSection(
    title: String,
    content: @Composable () -> Unit
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