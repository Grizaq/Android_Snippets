package com.chirilglance.androidglancedna.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chirilglance.androidglancedna.domain.models.profile.UserRoleType
import com.chirilglance.androidglancedna.presentation.common.useActiveProfile
import com.chirilglance.androidglancedna.presentation.examples.profile.ProfileImage
import com.chirilglance.androidglancedna.presentation.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    // Get the active profile
    val activeProfile = useActiveProfile()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Active profile section
        if (activeProfile != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProfileImage(
                                profile = activeProfile,
                                size = 48.dp
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "Welcome, ${activeProfile.displayName}",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = "Role: ${activeProfile.roleType.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Role-specific message
                        val roleMessage = when (activeProfile.roleType) {
                            UserRoleType.ADMIN -> "You have access to all admin features."
                            UserRoleType.PREMIUM -> "Enjoy your premium features and content."
                            UserRoleType.CREATOR -> "Access your creator tools and analytics."
                            UserRoleType.STANDARD -> "Browse through available features below."
                            else -> "Explore the application features below."
                        }

                        Text(
                            text = roleMessage,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Component examples title
        item {
            Text(
                text = "Component Examples",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // List of example items
        items(getExampleItems()) { example ->
            ExampleItem(
                title = example.title,
                description = example.description,
                onClick = { navController.navigate(example.route.route) }
            )
        }
    }
}

@Composable
private fun ExampleItem(
    title: String, description: String, onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(), onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title, style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description, style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun getExampleItems() = listOf(
    ExampleItem(
        "UI Components", "Buttons, text fields, cards and other UI elements", Screen.UiComponents
    ),
    ExampleItem("Form Validation", "Input validation examples", Screen.FormValidation),
    ExampleItem("Authentication", "OTP verification flow", Screen.Authentication),
    ExampleItem("Location Services", "Places API integration", Screen.LocationServices),
    ExampleItem(
        "Profile Management",
        "Secure profile storage with role-based features",
        Screen.ProfileManagement
    )
    // Add more examples as you implement them
)

private data class ExampleItem(
    val title: String, val description: String, val route: Screen
)