package com.chirilglance.androidglancedna.presentation.examples.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.chirilglance.androidglancedna.presentation.examples.ui.navigation.UIComponentsNavigation

/**
 * Main container for UI Components section with nested navigation
 */
@Composable
fun UIComponentsScreen() {
    val navController = rememberNavController()
    UIComponentsNavigation(navController)
}