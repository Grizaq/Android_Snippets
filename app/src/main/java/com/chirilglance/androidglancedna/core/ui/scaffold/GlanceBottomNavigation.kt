package com.chirilglance.androidglancedna.core.ui.scaffold

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chirilglance.androidglancedna.presentation.navigation.Screen

@Composable
fun GlanceBottomNavigation(
    navController: NavController,
    currentRoute: String,
    modifier: Modifier = Modifier
) {
    val navItems = remember { getNavItems() }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp,
        modifier = modifier
    ) {
        navItems.forEach { item ->
            val baseRoute = currentRoute.split("/")[0]
            val selected = baseRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(text = item.title) },
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

private fun getNavItems(): List<BottomNavItem> {
    return listOf(
        BottomNavItem(
            title = "Home",
            icon = Icons.Default.Home,
            route = Screen.Home.route
        ),
        BottomNavItem(
            title = "Components",
            icon = Icons.Default.Build,
            route = Screen.UiComponents.route
        ),
        BottomNavItem(
            title = "Auth",
            icon = Icons.Default.Lock,
            route = Screen.Authentication.route
        ),
        BottomNavItem(
            title = "Location",
            icon = Icons.Default.LocationOn,
            route = Screen.LocationServices.route
        )
    )
}