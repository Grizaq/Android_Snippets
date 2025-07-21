package com.chirilglance.androidglancedna.core.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.chirilglance.androidglancedna.core.ui.components.buttons.PrimaryButton
import com.chirilglance.androidglancedna.core.ui.components.buttons.SecondaryButton

/**
 * A customizable dialog component that follows the ClubConnect design system.
 *
 * @param onDismissRequest Callback to handle dialog dismissal when clicking outside
 * @param title Optional title text for the dialog
 * @param message Optional message text for the dialog body
 * @param customContent Optional composable for custom dialog content (prioritized over message)
 * @param confirmButton Optional custom composable for the confirm button
 * @param dismissButton Optional custom composable for the dismiss button
 * @param confirmButtonText Text for the default confirm button if [confirmButton] is not provided
 * @param dismissButtonText Text for the default dismiss button if [dismissButton] is not provided
 * @param showConfirmButton Whether to show the confirm button
 * @param showDismissButton Whether to show the dismiss button
 * @param onConfirmClick Callback for confirm button click
 * @param onDismissClick Callback for dismiss button click (defaults to onDismissRequest)
 * @param containerColor Background color for the dialog
 * @param titleTextStyle TextStyle for the title
 * @param messageTextStyle TextStyle for the message
 * @param maxContentHeight Maximum height for the content area with scrolling enabled if exceeded
 */
@Composable
fun DefaultDialog(
    onDismissRequest: () -> Unit,

    // Content
    title: String? = null,
    message: String? = null,
    customContent: @Composable (() -> Unit)? = null,

    // Buttons
    confirmButton: @Composable (() -> Unit)? = null,
    dismissButton: @Composable (() -> Unit)? = null,

    // Default button parameters
    confirmButtonText: String = "Confirm",
    dismissButtonText: String = "Cancel",
    showConfirmButton: Boolean = true,
    showDismissButton: Boolean = true,
    onConfirmClick: () -> Unit = {},
    onDismissClick: () -> Unit = onDismissRequest,

    // Styling
    containerColor: Color = MaterialTheme.colorScheme.surface,
    titleTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
    messageTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,

    // Layout
    maxContentHeight: Dp = 300.dp,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), colors = CardDefaults.cardColors(
                containerColor = containerColor
            ), elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Title
                if (title != null) {
                    Text(
                        text = title,
                        style = titleTextStyle,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Content Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    if (customContent != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = maxContentHeight)
                                .verticalScroll(rememberScrollState())
                        ) {
                            customContent()
                        }
                    } else if (message != null) {
                        Text(
                            text = message,
                            style = messageTextStyle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if ((showConfirmButton && confirmButton == null) || (showDismissButton && dismissButton == null) || confirmButton != null || dismissButton != null) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                    ) {
                        // Dismiss Button
                        if (showDismissButton) {
                            if (dismissButton != null) {
                                dismissButton()
                            } else {
                                SecondaryButton(
                                    text = dismissButtonText, onClick = onDismissClick
                                )
                            }
                        }

                        // Space between buttons if both are shown
                        if (showConfirmButton && showDismissButton) {
                            Spacer(modifier = Modifier.width(16.dp))
                        }

                        // Confirm Button
                        if (showConfirmButton) {
                            if (confirmButton != null) {
                                confirmButton()
                            } else {
                                PrimaryButton(text = confirmButtonText, onClick = {
                                    onConfirmClick()
                                    onDismissRequest()
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * A stateful version of [DefaultDialog] that manages its own visibility state.
 *
 * @param showDialog Boolean state to control dialog visibility
 * @param onDialogDismiss Callback when the dialog is dismissed
 * @param other parameters Same as [DefaultDialog]
 */
@Composable
fun DefaultDialogStateful(
    // State
    showDialog: Boolean,
    onDialogDismiss: () -> Unit,

    // Content
    title: String? = null,
    message: String? = null,
    customContent: @Composable (() -> Unit)? = null,

    // Buttons
    confirmButton: @Composable (() -> Unit)? = null,
    dismissButton: @Composable (() -> Unit)? = null,

    // Default button parameters
    confirmButtonText: String = "Confirm",
    dismissButtonText: String = "Cancel",
    showConfirmButton: Boolean = true,
    showDismissButton: Boolean = true,
    onConfirmClick: () -> Unit = {},
    onDismissClick: () -> Unit = onDialogDismiss,

    // Styling
    containerColor: Color = MaterialTheme.colorScheme.surface,
    titleTextStyle: TextStyle = MaterialTheme.typography.titleLarge,
    messageTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,

    // Layout
    maxContentHeight: Dp = 300.dp,
) {
    if (showDialog) {
        DefaultDialog(
            onDismissRequest = onDialogDismiss,
            title = title,
            message = message,
            customContent = customContent,
            confirmButton = confirmButton,
            dismissButton = dismissButton,
            confirmButtonText = confirmButtonText,
            dismissButtonText = dismissButtonText,
            showConfirmButton = showConfirmButton,
            showDismissButton = showDismissButton,
            onConfirmClick = onConfirmClick,
            onDismissClick = onDismissClick,
            containerColor = containerColor,
            titleTextStyle = titleTextStyle,
            messageTextStyle = messageTextStyle,
            maxContentHeight = maxContentHeight
        )
    }
}

/**
 * Convenience object with default settings for common dialog types.
 */
object DefaultsDialog {
    /**
     * Creates a simple notification dialog with a single button.
     *
     * @param title Dialog title
     * @param message Dialog message
     * @param buttonText Text for the single button (default: "OK")
     * @param onDismiss Callback when dialog is dismissed
     */
    @Composable
    fun NotificationDialog(
        title: String, message: String, buttonText: String = "OK", onDismiss: () -> Unit
    ) {
        DefaultDialog(onDismissRequest = onDismiss,
            title = title,
            message = message,
            confirmButtonText = buttonText,
            showDismissButton = false,
            onConfirmClick = {})
    }

    /**
     * Creates a confirmation dialog with confirm and cancel buttons.
     *
     * @param title Dialog title
     * @param message Dialog message
     * @param confirmText Text for the confirm button (default: "Confirm")
     * @param cancelText Text for the cancel button (default: "Cancel")
     * @param onConfirm Callback when confirm button is clicked
     * @param onDismiss Callback when dialog is dismissed
     */
    @Composable
    fun ConfirmationDialog(
        title: String,
        message: String,
        confirmText: String = "Confirm",
        cancelText: String = "Cancel",
        onConfirm: () -> Unit,
        onDismiss: () -> Unit
    ) {
        DefaultDialog(
            onDismissRequest = onDismiss,
            title = title,
            message = message,
            confirmButtonText = confirmText,
            dismissButtonText = cancelText,
            onConfirmClick = onConfirm
        )
    }
}