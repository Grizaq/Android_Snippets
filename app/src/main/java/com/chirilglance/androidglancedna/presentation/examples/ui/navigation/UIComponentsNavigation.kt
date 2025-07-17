package com.chirilglance.androidglancedna.presentation.examples.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chirilglance.androidglancedna.presentation.examples.ui.UIComponentsCategoryScreen
import com.chirilglance.androidglancedna.presentation.examples.ui.components.ButtonsScreen
import com.chirilglance.androidglancedna.presentation.examples.ui.components.CardsScreen
import com.chirilglance.androidglancedna.presentation.examples.ui.components.DialogsScreen
import com.chirilglance.androidglancedna.presentation.examples.ui.components.SnackbarsScreen
import com.chirilglance.androidglancedna.presentation.examples.ui.components.TextFieldsScreen

/**
 * Nested navigation for UI Components section
 */
@Composable
fun UIComponentsNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = UIComponentsRoutes.CATEGORIES
    ) {
        composable(UIComponentsRoutes.CATEGORIES) {
            UIComponentsCategoryScreen(navController)
        }

        composable(UIComponentsRoutes.DIALOGS) {
            DialogsScreen()
        }

        composable(UIComponentsRoutes.BUTTONS) {
            ButtonsScreen()
        }

        composable(UIComponentsRoutes.TEXT_FIELDS) {
            TextFieldsScreen()
        }

        composable(UIComponentsRoutes.CARDS) {
            CardsScreen()
        }

        composable(UIComponentsRoutes.SNACKBARS) {
            SnackbarsScreen()
        }
    }
}

/**
 * Routes for UI Components nested navigation
 */
object UIComponentsRoutes {
    const val CATEGORIES = "categories"
    const val DIALOGS = "dialogs"
    const val BUTTONS = "buttons"
    const val TEXT_FIELDS = "text_fields"
    const val CARDS = "cards"
    const val SNACKBARS = "snackbars"
}