package com.chirilglance.androidglancedna.presentation.examples.auth.welcomescreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chirilglance.androidglancedna.core.ui.components.DefaultCard
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectCardDefaults

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            DefaultCard(
                modifier = Modifier.align(Alignment.Center),
                titleContent = ClubConnectCardDefaults.Title("Verification Successful!"),
                subtitleContent = ClubConnectCardDefaults.Subtitle("Welcome to Android Glance DNA"),
                // Accessibility options
                descriptionContent = ClubConnectCardDefaults.Description(
                    "Your phone number has been verified successfully. You can now access all features of the application."
                )
            )

            // Bottom action button
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Text("Get Started")
            }
        }
    }
}