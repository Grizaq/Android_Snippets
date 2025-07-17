package com.chirilglance.androidglancedna.presentation.examples.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chirilglance.androidglancedna.presentation.examples.ui.navigation.UIComponentsRoutes

@Composable
fun UIComponentsCategoryScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(getComponentCategories()) { category ->
            ComponentCategoryItem(title = category.title,
                description = category.description,
                icon = category.icon,
                onClick = { navController.navigate(category.route) })
        }
    }
}

@Composable
private fun ComponentCategoryItem(
    title: String, description: String, icon: ImageVector, onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick, modifier = Modifier.fillMaxWidth()
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )

            androidx.compose.foundation.layout.Column {
                Text(
                    text = title, style = MaterialTheme.typography.titleLarge
                )

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description, style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun getComponentCategories() = listOf(
    ComponentCategory(
        "Dialogs",
        "Notification, confirmation and custom dialogs",
        Icons.Default.MailOutline,
        UIComponentsRoutes.DIALOGS
    ), ComponentCategory(
        "Buttons",
        "Primary, secondary and specialized buttons",
        Icons.Default.CheckCircle,
        UIComponentsRoutes.BUTTONS
    ), ComponentCategory(
        "Text Fields",
        "Input fields with validation and styling",
        Icons.Default.Edit,
        UIComponentsRoutes.TEXT_FIELDS
    ), ComponentCategory(
        "Cards",
        "Information cards with various layouts and features",
        Icons.Default.AccountBox,
        UIComponentsRoutes.CARDS
    ), ComponentCategory(
        "Snackbars",
        "Toast messages with various configurations",
        Icons.Default.Notifications,
        UIComponentsRoutes.SNACKBARS
    )
)

private data class ComponentCategory(
    val title: String, val description: String, val icon: ImageVector, val route: String
)