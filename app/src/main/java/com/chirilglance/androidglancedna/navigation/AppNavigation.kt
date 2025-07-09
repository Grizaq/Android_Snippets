package com.chirilglance.androidglancedna.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chirilglance.androidglancedna.examples.auth.AuthenticationScreen
import com.chirilglance.androidglancedna.examples.forms.FormValidationScreen
import com.chirilglance.androidglancedna.examples.location.LocationServiceScreen
import com.chirilglance.androidglancedna.examples.ui.UIComponentsScreen
import com.chirilglance.androidglancedna.home.HomeScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.HOME
    ) {
        composable(NavigationRoutes.HOME) {
            HomeScreen(navController)
        }

        // Example screens
        composable(NavigationRoutes.UI_COMPONENTS) {
            UIComponentsScreen()
        }

        composable(NavigationRoutes.FORM_VALIDATION) {
            FormValidationScreen()
        }

        composable(NavigationRoutes.AUTHENTICATION) {
            AuthenticationScreen()
        }

        composable(NavigationRoutes.LOCATION_SERVICES) {
            LocationServiceScreen()
        }
    }
}