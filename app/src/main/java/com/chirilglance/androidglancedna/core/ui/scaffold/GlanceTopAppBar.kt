package com.chirilglance.androidglancedna.core.ui.scaffold

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.chirilglance.androidglancedna.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlanceTopAppBar(
    navController: NavController,
    currentRoute: String,
    modifier: Modifier = Modifier
) {
    // Get screen title based on route
    val title = getScreenTitle(currentRoute)

    TopAppBar(
        title = {
            Text(
                text = title,
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            // Show back button if not on the Home screen
            if (currentRoute != Screen.Home.route && navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

// Helper function to get screen title based on route
private fun getScreenTitle(route: String): String {
    val baseRoute = route.split("/")[0]

    return when (baseRoute) {
        Screen.Home.route -> "Android Glance DNA"
        Screen.UiComponents.route -> "UI Components"
        Screen.FormValidation.route -> "Form Validation"
        Screen.Authentication.route -> "Authentication"
        Screen.LocationServices.route -> "Location Services"
        Screen.StateManagement.route -> "State Management"
        else -> "Android Glance DNA"
    }
}