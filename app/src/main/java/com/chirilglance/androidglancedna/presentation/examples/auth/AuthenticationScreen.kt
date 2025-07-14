package com.chirilglance.androidglancedna.presentation.examples.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chirilglance.androidglancedna.core.ui.scaffold.NavigationState
import com.chirilglance.androidglancedna.presentation.examples.auth.otpscreen.OtpVerificationScreen
import com.chirilglance.androidglancedna.presentation.examples.auth.phoneverificationscreen.PhoneVerificationScreen
import com.chirilglance.androidglancedna.presentation.examples.auth.welcomescreen.WelcomeScreen
import com.chirilglance.androidglancedna.presentation.navigation.Screen

/**
 * Main authentication screen that contains nested navigation for the authentication flow
 *
 * @param mainNavController Optional main navigation controller to navigate outside the auth flow
 */
@Composable
fun AuthenticationScreen(
    mainNavController: NavHostController? = null
) {
    // Create a nested navigation controller for the authentication flow
    val authNavController = rememberNavController()

    // Nested navigation host
    NavHost(
        navController = authNavController,
        startDestination = Screen.PhoneVerification.route
    ) {
        composable(Screen.PhoneVerification.route) {
            PhoneVerificationScreen(
                onVerificationRequested = { phoneNumber ->
                    authNavController.navigate(Screen.OtpVerification.createRoute(phoneNumber))
                }
            )
        }

        composable(
            route = Screen.OtpVerification.route,
            arguments = listOf(
                navArgument("phoneNumber") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            OtpVerificationScreen(
                phoneNumber = phoneNumber,
                onVerificationComplete = {
                    // Navigate to welcome screen
                    authNavController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.PhoneVerification.route) { inclusive = true }
                    }
                },
                onBackPressed = {
                    authNavController.popBackStack()
                }
            )
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStarted = {
                    // Navigate to the main app home screen
                    if (mainNavController != null) {
                        // If we have access to the main navigation controller,
                        // navigate directly to the home screen
                        mainNavController.navigate(Screen.Home.route) {
                            // Clear all back stack entries
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        // For testing or standalone use, just reset the auth flow
                        authNavController.navigate(Screen.PhoneVerification.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}