package com.chirilglance.androidglancedna.presentation.examples.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chirilglance.androidglancedna.core.ui.components.buttons.PrimaryButton
import com.chirilglance.androidglancedna.core.ui.components.buttons.SecondaryButton
import com.chirilglance.androidglancedna.presentation.examples.ui.ComponentSection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ButtonsScreen() {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Button Examples", style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Buttons allow users to take actions and make choices with a single tap.",
            style = MaterialTheme.typography.bodyLarge
        )

        // Primary Buttons
        ComponentSection(title = "Primary Buttons") {
            Text(
                text = "Primary buttons are high-emphasis buttons that represent the main action.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Standard Primary Button
            PrimaryButton(
                text = "Standard Primary Button",
                onClick = { /* Handle click */ },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Loading Primary Button
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

            // Disabled Primary Button
            PrimaryButton(
                text = "Disabled Primary Button",
                onClick = { /* Handle click */ },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Secondary Buttons
        ComponentSection(title = "Secondary Buttons") {
            Text(
                text = "Secondary buttons are medium-emphasis buttons often used for secondary actions.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Standard Secondary Button
            SecondaryButton(
                text = "Standard Secondary Button",
                onClick = { /* Handle click */ },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Disabled Secondary Button
            SecondaryButton(
                text = "Disabled Secondary Button",
                onClick = { /* Handle click */ },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Custom Colored Buttons
        ComponentSection(title = "Custom Colored Buttons") {
            Text(
                text = "Buttons can be customized with different colors to match your brand or indicate different actions.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Custom colored primary button
            PrimaryButton(
                text = "Primary Button - Indigo",
                onClick = { /* Handle click */ },
                containerColor = Color(0xFF3F51B5), // Indigo
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Custom colored secondary button
            SecondaryButton(
                text = "Secondary Button - Indigo",
                onClick = { /* Handle click */ },
                contentColor = Color(0xFF3F51B5), // Indigo
                borderColor = Color(0xFF3F51B5), // Indigo
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Red primary button (for destructive actions)
            PrimaryButton(
                text = "Destructive Action",
                onClick = { /* Handle click */ },
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}