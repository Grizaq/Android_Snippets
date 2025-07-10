package com.chirilglance.androidglancedna.presentation.examples.auth.welcomescreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectCard

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
            ClubConnectCard(
                modifier = Modifier.align(Alignment.Center),
                title = "Verification Successful!",
                subtitle = "Welcome to Android Glance DNA",
                description = "Your phone number has been verified successfully. You can now access all features of the application."
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