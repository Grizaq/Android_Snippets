package com.chirilglance.androidglancedna.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chirilglance.androidglancedna.presentation.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Component Examples",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(getExampleItems()) { example ->
            ExampleItem(title = example.title,
                description = example.description,
                onClick = { navController.navigate(example.route.route) })
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
    ExampleItem("Location Services", "Places API integration", Screen.LocationServices)
    // Add more examples as you implement them
)

private data class ExampleItem(
    val title: String, val description: String, val route: Screen
)