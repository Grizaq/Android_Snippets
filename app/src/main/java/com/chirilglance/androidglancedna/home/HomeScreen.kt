package com.chirilglance.androidglancedna.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chirilglance.androidglancedna.navigation.NavigationRoutes

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Android Glance DNA") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                ExampleItem(
                    title = example.title,
                    description = example.description,
                    onClick = { navController.navigate(example.route) }
                )
            }
        }
    }
}

@Composable
private fun ExampleItem(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun getExampleItems() = listOf(
    ExampleItem("UI Components", "Buttons, text fields, cards and other UI elements", NavigationRoutes.UI_COMPONENTS),
    ExampleItem("Form Validation", "Input validation examples", NavigationRoutes.FORM_VALIDATION),
    ExampleItem("Authentication", "OTP verification flow", NavigationRoutes.AUTHENTICATION),
    ExampleItem("Location Services", "Places API integration", NavigationRoutes.LOCATION_SERVICES)
    // Add more examples as you implement them
)

private data class ExampleItem(
    val title: String,
    val description: String,
    val route: String
)