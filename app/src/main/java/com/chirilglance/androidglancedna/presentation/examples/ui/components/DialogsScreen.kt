package com.chirilglance.androidglancedna.presentation.examples.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DialogsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Dialog Examples", style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Dialogs provide important information, require decisions, or involve multiple tasks. Each type of dialog presents different options and interactions.",
            style = MaterialTheme.typography.bodyLarge
        )

        // Dialog examples
        DialogsSection()
    }
}