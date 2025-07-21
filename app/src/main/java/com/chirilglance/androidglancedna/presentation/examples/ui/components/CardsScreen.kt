package com.chirilglance.androidglancedna.presentation.examples.ui.components

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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chirilglance.androidglancedna.core.ui.components.DefaultCard
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectCardDefaults
import com.chirilglance.androidglancedna.core.ui.components.buttons.PrimaryButton
import com.chirilglance.androidglancedna.core.ui.components.buttons.SecondaryButton
import com.chirilglance.androidglancedna.presentation.examples.ui.ComponentSection

@Composable
fun CardsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Card Examples", style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Cards contain content and actions about a single subject. They're used to group related information and tasks.",
            style = MaterialTheme.typography.bodyLarge
        )

        // Basic Cards
        ComponentSection(title = "Basic Cards") {
            Text(
                text = "Simple cards with title and description.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Basic Card
            DefaultCard(
                titleContent = ClubConnectCardDefaults.Title("Basic Card"),
                descriptionContent = ClubConnectCardDefaults.Description("This is a basic card with title and description."),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Cards with Icons
        ComponentSection(title = "Cards with Icons") {
            Text(
                text = "Cards can include icons to provide visual cues.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Card with Icon
            DefaultCard(
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
        }

        // Interactive Cards
        ComponentSection(title = "Interactive Cards") {
            Text(
                text = "Cards can be clickable to trigger actions.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Clickable Card
            var clickCount by remember { mutableIntStateOf(0) }
            DefaultCard(
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
        }

        // Styled Cards
        ComponentSection(title = "Styled Cards") {
            Text(
                text = "Cards can be styled with custom colors to match your brand or indicate status.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Colored Card
            DefaultCard(titleContent = {
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
        }

        // Cards with Actions
        ComponentSection(title = "Cards with Actions") {
            Text(
                text = "Cards can include action buttons in different layouts.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Card with Horizontal Action Buttons
            DefaultCard(
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
            DefaultCard(
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