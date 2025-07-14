package com.chirilglance.androidglancedna.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chirilglance.androidglancedna.presentation.examples.auth.AuthenticationScreen
import com.chirilglance.androidglancedna.presentation.examples.forms.FormValidationScreen
import com.chirilglance.androidglancedna.presentation.examples.location.LocationServiceScreen
import com.chirilglance.androidglancedna.presentation.examples.ui.UIComponentsScreen
import com.chirilglance.androidglancedna.presentation.home.HomeScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        // Example screens
        composable(Screen.UiComponents.route) {
            UIComponentsScreen()
        }

        composable(Screen.FormValidation.route) {
            FormValidationScreen()
        }

        composable(Screen.Authentication.route) {
            AuthenticationScreen(mainNavController = navController)
        }

        composable(Screen.LocationServices.route) {
            LocationServiceScreen()
        }

        composable(Screen.StateManagement.route) {
            // planned
        }
    }
}