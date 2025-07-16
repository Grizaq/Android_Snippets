package com.chirilglance.androidglancedna.core.ui.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.chirilglance.androidglancedna.core.ui.components.GlanceSnackbarHost
import com.chirilglance.androidglancedna.presentation.navigation.Screen

@Composable
fun GlanceAppScaffold(
    navController: NavHostController, modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Get current route to determine navigation state
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    // Determine which navigation elements to show
    val navigationState = remember(currentRoute) {
        getNavigationState(currentRoute)
    }

    Scaffold(modifier = modifier.fillMaxSize(), snackbarHost = {
        GlanceSnackbarHost(hostState = snackbarHostState)
    }, topBar = {
        if (navigationState.showTopBar) {
            GlanceTopAppBar(
                navController = navController, currentRoute = currentRoute
            )
        }
    }, bottomBar = {
        if (navigationState.showBottomNav) {
            GlanceBottomNavigation(
                navController = navController, currentRoute = currentRoute
            )
        }
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            content()
        }
    }
}

// Navigation state class to represent UI state
data class NavigationState(
    val showTopBar: Boolean, val showBottomNav: Boolean
)

// Helper function to determine navigation state based on route
private fun getNavigationState(currentRoute: String): NavigationState {
    // Handle routes with parameters
    val baseRoute = when {
        currentRoute.contains("/") -> currentRoute.split("/")[0]
        else -> currentRoute
    }

    // Define screens that require special navigation handling
    return when (baseRoute) {
        // Screens with no navigation elements
        Screen.PhoneVerification.route, Screen.OtpVerification.route.split("/")[0], Screen.Welcome.route -> NavigationState(
            showTopBar = false,
            showBottomNav = false
        )

        // Screens with only top bar
        Screen.FormValidation.route -> NavigationState(showTopBar = true, showBottomNav = false)

        // Screens with only bottom bar
        // Add your screens here

        // Default: show both navigation elements
        else -> NavigationState(showTopBar = true, showBottomNav = true)
    }
}