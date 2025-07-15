package com.chirilglance.androidglancedna.core.ui.scaffold

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chirilglance.androidglancedna.domain.models.profile.UserProfile
import com.chirilglance.androidglancedna.domain.models.profile.UserRoleType
import com.chirilglance.androidglancedna.presentation.common.useActiveProfile
import com.chirilglance.androidglancedna.presentation.examples.profile.ProfileManagementViewModel
import com.chirilglance.androidglancedna.presentation.navigation.Screen

/**
 * Role-based bottom navigation that adapts based on the active user profile.
 */
@Composable
fun GlanceBottomNavigation(
    navController: NavController,
    currentRoute: String,
    viewModel: ProfileManagementViewModel = hiltViewModel()
) {
    val activeProfile = useActiveProfile()

    // Get navigation items based on the active role
    val navigationItems = getNavigationItemsForRole(activeProfile)

    NavigationBar {
        navigationItems.forEach { item ->
            val isSelected = currentRoute.startsWith(item.route)

            NavigationBarItem(icon = {
                Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.title
                )
            }, label = { Text(item.title) }, selected = isSelected, onClick = {
                if (!isSelected) {
                    navController.navigate(item.route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            })
        }
    }
}

/**
 * Returns the appropriate navigation items based on the active user role.
 */
private fun getNavigationItemsForRole(activeProfile: UserProfile?): List<NavigationItem> {
    // Default navigation items for all users
    val defaultItems = listOf(
        NavigationItem(
            title = "Home",
            route = Screen.Home.route,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ), NavigationItem(
            title = "Profile",
            route = Screen.ProfileManagement.route,
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person
        )
    )

    // If no active profile, return default items
    if (activeProfile == null) {
        return defaultItems
    }

    // Add role-specific navigation items
    return when (activeProfile.roleType) {
        UserRoleType.STANDARD -> defaultItems + listOf(
            NavigationItem(
                title = "Search",
                route = Screen.FormValidation.route, // Just for demo, using existing screens
                selectedIcon = Icons.Filled.Search,
                unselectedIcon = Icons.Outlined.Search
            )
        )

        UserRoleType.PREMIUM -> defaultItems + listOf(
            NavigationItem(
                title = "Search",
                route = Screen.FormValidation.route,
                selectedIcon = Icons.Filled.Search,
                unselectedIcon = Icons.Outlined.Search
            ), NavigationItem(
                title = "Settings", route = Screen.Authentication.route, // Just for demo
                selectedIcon = Icons.Filled.Settings, unselectedIcon = Icons.Outlined.Settings
            )
        )

        UserRoleType.ADMIN -> listOf(
            NavigationItem(
                title = "Dashboard",
                route = Screen.Home.route,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            ), NavigationItem(
                title = "Users",
                route = Screen.ProfileManagement.route,
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person
            ), NavigationItem(
                title = "Settings", route = Screen.Authentication.route, // Just for demo
                selectedIcon = Icons.Filled.Settings, unselectedIcon = Icons.Outlined.Settings
            )
        )

        UserRoleType.CREATOR -> listOf(
            NavigationItem(
                title = "Home",
                route = Screen.Home.route,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            ), NavigationItem(
                title = "Create", route = Screen.UiComponents.route, // Just for demo
                selectedIcon = Icons.Filled.Add, unselectedIcon = Icons.Filled.Add
            ), NavigationItem(
                title = "Profile",
                route = Screen.ProfileManagement.route,
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person
            )
        )

        // VIEWER role and any others
        else -> defaultItems
    }
}

/**
 * Data class representing a navigation item.
 */
data class NavigationItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)