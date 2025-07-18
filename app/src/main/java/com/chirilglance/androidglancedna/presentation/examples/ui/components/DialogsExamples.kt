package com.chirilglance.androidglancedna.presentation.examples.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chirilglance.androidglancedna.core.ui.components.buttons.PrimaryButton
import com.chirilglance.androidglancedna.core.ui.components.buttons.SecondaryButton
import com.chirilglance.androidglancedna.core.ui.components.dialogs.DefaultDialog
import com.chirilglance.androidglancedna.core.ui.components.dialogs.DefaultsDialog
import com.chirilglance.androidglancedna.core.ui.components.dialogs.DefaultDialogStateful
import com.chirilglance.androidglancedna.presentation.examples.ui.ComponentSection

@Composable
fun DialogsSection() {
    ComponentSection(title = "Dialogs") {
        Text(
            text = "Dialogs provide important prompts in a focused UI element.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Basic Dialog Example
        SimpleNotificationDialogExample()

        Spacer(modifier = Modifier.height(16.dp))

        // Confirmation Dialog Example
        ConfirmationDialogExample()

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Content Dialog Example
        CustomContentDialogExample()

        Spacer(modifier = Modifier.height(16.dp))

        // Stateful Dialog Example
        StatefulDialogExample()
    }
}

@Composable
private fun SimpleNotificationDialogExample() {
    var showDialog by remember { mutableStateOf(false) }

    Text(
        text = "Simple Notification Dialog",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Text(
        text = "A simple dialog with a message and a single action button.",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    PrimaryButton(
        text = "Show Notification Dialog",
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth()
    )

    if (showDialog) {
        DefaultsDialog.NotificationDialog(title = "Operation Successful",
            message = "Your changes have been saved successfully.",
            buttonText = "OK",
            onDismiss = { showDialog = false })
    }
}

@Composable
private fun ConfirmationDialogExample() {
    var showDialog by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("No action taken yet") }

    Text(
        text = "Confirmation Dialog",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Text(
        text = "A dialog with confirm and cancel buttons.",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        SecondaryButton(
            text = "Show Confirmation Dialog",
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Result: $result",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(8.dp)
        )
    }

    if (showDialog) {
        DefaultDialog(
            onDismissRequest = { showDialog = false },
            title = "Confirm Action",
            message = "Are you sure you want to proceed with this action? This cannot be undone.",
            confirmButtonText = "Continue",
            dismissButtonText = "Cancel",
            onConfirmClick = {
                result = "Action confirmed!"
                showDialog = false
            },
            onDismissClick = {
                result = "Action cancelled"
                showDialog = false
            },
            // Custom styling example
            titleTextStyle = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
private fun CustomContentDialogExample() {
    var showDialog by remember { mutableStateOf(false) }
    val users = remember {
        listOf(
            "John Smith",
            "Emma Johnson",
            "Michael Brown",
            "Olivia Davis",
            "William Wilson",
            "Sophia Martinez",
            "James Anderson",
            "Charlotte Thomas",
            "Benjamin Jackson",
            "Amelia White"
        )
    }
    val selectedUsers = remember { mutableStateListOf<String>() }

    Text(
        text = "Custom Content Dialog",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Text(
        text = "A dialog with custom content showing a list of selected users.",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // User selection UI (outside the dialog)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Select users to add:",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                users.take(5).forEach { user ->
                    val isSelected = selectedUsers.contains(user)
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable {
                            if (isSelected) {
                                selectedUsers.remove(user)
                            } else {
                                selectedUsers.add(user)
                            }
                        }
                        .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = user, style = MaterialTheme.typography.bodyMedium
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to open confirmation dialog
        PrimaryButton(
            text = "Confirm Selected Users", onClick = {
                if (selectedUsers.isNotEmpty()) {
                    showDialog = true
                }
            }, enabled = selectedUsers.isNotEmpty(), modifier = Modifier.fillMaxWidth()
        )
    }

    // Confirmation dialog showing selected users
    if (showDialog) {
        DefaultDialog(onDismissRequest = { showDialog = false },
            title = "Confirm Selected Users",
            customContent = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "You are about to add the following users:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        selectedUsers.forEach { user ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = user, style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This action will grant them access to the project.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButtonText = "Add Users",
            dismissButtonText = "Cancel",
            onConfirmClick = {
                // In a real app, this would process the selected users
                showDialog = false
            })
    }
}

@Composable
private fun StatefulDialogExample() {
    // Using stateful dialog with automatic state management
    var showDialog by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("No warnings active") }

    Text(
        text = "Stateful Dialog",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Text(
        text = "A dialog that manages its own visibility state.",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        SecondaryButton(text = "Show Warning",
            onClick = { showDialog = true },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            contentColor = MaterialTheme.colorScheme.error,
            borderColor = MaterialTheme.colorScheme.error,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Status: $status",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }

    DefaultDialogStateful(
        showDialog = showDialog,
        onDialogDismiss = { showDialog = false },
        title = "Warning",
        message = "The system has detected unusual activity. Would you like to activate enhanced security measures?",
        confirmButtonText = "Activate",
        dismissButtonText = "Ignore",
        onConfirmClick = {
            status = "Enhanced security activated"
            showDialog = false
        },
        onDismissClick = {
            status = "Warning ignored"
            showDialog = false
        },
        confirmButton = {
            PrimaryButton(
                text = "Activate",
                onClick = {
                    status = "Enhanced security activated"
                    showDialog = false
                },
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        },
        titleTextStyle = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.error)
    )
}