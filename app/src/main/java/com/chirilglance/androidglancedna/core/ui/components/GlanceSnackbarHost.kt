package com.chirilglance.androidglancedna.core.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chirilglance.androidglancedna.presentation.ui.theme.ClubConnectGreen
import com.chirilglance.androidglancedna.presentation.ui.theme.ErrorRed
import com.chirilglance.androidglancedna.presentation.ui.theme.Navy
import kotlinx.coroutines.launch
import com.chirilglance.androidglancedna.core.ui.components.SnackbarDuration as LocalSnackbarDuration

@Composable
fun GlanceSnackbarHost(
    modifier: Modifier = Modifier, hostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val snackbarState by SnackbarManager.snackbarState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Listen for snackbar state changes
    LaunchedEffect(snackbarState) {
        when (val state = snackbarState) {
            is SnackbarState.Hidden -> {
                hostState.currentSnackbarData?.dismiss()
            }

            is SnackbarState.Visible -> {
                val result = hostState.showSnackbar(
                    message = state.message,
                    actionLabel = state.actionLabel,
                    duration = when (state.duration) {
                        LocalSnackbarDuration.SHORT -> SnackbarDuration.Short
                        LocalSnackbarDuration.LONG -> SnackbarDuration.Long
                        LocalSnackbarDuration.INDEFINITE -> SnackbarDuration.Indefinite
                    }
                )
                when (result) {
                    SnackbarResult.Dismissed -> SnackbarManager.hideSnackbar()
                    SnackbarResult.ActionPerformed -> {
                        state.onAction?.invoke()
                        SnackbarManager.hideSnackbar()
                    }
                }
            }
        }
    }

    SnackbarHost(hostState = hostState, modifier = modifier, snackbar = { snackbarData ->
        // Get current snackbar state to determine styling
        val currentVisibleState = when (snackbarState) {
            is SnackbarState.Visible -> (snackbarState as SnackbarState.Visible)
            else -> null
        }

        // Custom styling based on type
        val backgroundColor = when (currentVisibleState?.type) {
            SnackbarType.ERROR -> ErrorRed
            SnackbarType.SUCCESS -> ClubConnectGreen
            SnackbarType.INFO, null -> Navy
        }

        // Text color should contrast with background
        val textColor = when (currentVisibleState?.type) {
            SnackbarType.ERROR -> Color.White
            SnackbarType.SUCCESS -> Navy  // Dark text on light background
            SnackbarType.INFO, null -> Color.White
            else -> Color.White
        }

        Snackbar(modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            action = snackbarData.visuals.actionLabel?.let { actionLabel ->
                {
                    TextButton(
                        onClick = { snackbarData.performAction() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = textColor
                        )
                    ) {
                        Text(
                            text = actionLabel, style = TextStyle(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            },
            dismissAction = if (currentVisibleState?.showDismissButton == true) {
                {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            snackbarData.dismiss()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Dismiss",
                            tint = textColor
                        )
                    }
                }
            } else null,
            containerColor = backgroundColor,
            contentColor = textColor) {
            Text(snackbarData.visuals.message)
        }
    })
}