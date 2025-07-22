package com.chirilglance.androidglancedna.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Location input field with search icon
 */
@Composable
fun LocationField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    isLoading: Boolean = false,
    isActive: Boolean = false,
    enabled: Boolean = true,
    onLocationClick: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    DefaultTextField(
        value = value,
        onValueChange = onValueChange,
        label = "Location",
        hint = "Enter location...",
        errorMessage = errorMessage,
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onLocationClick() },
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Search,
        isActive = isActive,
        onDone = {
            focusManager.clearFocus()
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxWidth(0.05f),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        readOnly = !enabled
    )
}