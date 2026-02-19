package com.chirilglance.androidglancedna.presentation.examples.auth

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chirilglance.androidglancedna.presentation.examples.auth.emailscreen.EmailSignInScreen
import com.chirilglance.androidglancedna.presentation.examples.auth.otpscreen.OtpVerificationScreen
import com.chirilglance.androidglancedna.presentation.examples.auth.phoneverificationscreen.PhoneVerificationScreen
import com.chirilglance.androidglancedna.presentation.examples.auth.welcomescreen.WelcomeScreen
import com.chirilglance.androidglancedna.presentation.navigation.Screen

/**
 * Main authentication screen that contains nested navigation for the authentication flow
 *
 * @param mainNavController main navigation controller to navigate outside the auth flow
 */
@Composable
fun AuthenticationScreen(
    mainNavController: NavHostController
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
                navController = authNavController,
                onVerificationRequested = { phoneNumber, verificationId ->
                    // Navigate to OTP screen with phone number and verification ID
                    authNavController.navigate(
                        Screen.OtpVerification.createRoute(phoneNumber, verificationId)
                    )
                },
                onGoogleSignInSuccess = {
                    // User signed in with Google - go straight to welcome
                    authNavController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.PhoneVerification.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.EmailSignIn.route) {
            EmailSignInScreen(
                onSuccess = {
                    // Navigate to welcome screen after successful email auth
                    authNavController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.PhoneVerification.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.OtpVerification.route,
            arguments = listOf(
                navArgument("phoneNumber") { type = NavType.StringType },
                navArgument("verificationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            val verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""

            OtpVerificationScreen(
                phoneNumber = phoneNumber,
                verificationId = verificationId,
                resendToken = null, // Token will be managed by ViewModel
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
                    // navigate directly to the home screen
                    mainNavController.navigate(Screen.Home.route) {
                        // Clear all back stack entries
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}