package com.chirilglance.androidglancedna.presentation.examples.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectTextField
import com.chirilglance.androidglancedna.core.ui.components.LabeledTextField
import com.chirilglance.androidglancedna.core.ui.components.NumberTextField
import com.chirilglance.androidglancedna.presentation.examples.ui.ComponentSection

@Composable
fun TextFieldsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Text Field Examples", style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Text fields let users enter and edit text. They typically appear in forms and dialogs.",
            style = MaterialTheme.typography.bodyLarge
        )

        // Basic Text Fields
        ComponentSection(title = "Basic Text Fields") {
            Text(
                text = "Standard text input with label and hint text.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Basic Text Field
            var text by remember { mutableStateOf("") }
            ClubConnectTextField(
                value = text,
                onValueChange = { text = it },
                label = "Basic Text Field",
                hint = "Enter text here",
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Text Fields with Validation
        ComponentSection(title = "Text Fields with Validation") {
            Text(
                text = "Text fields can display error messages when validation fails.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Email Text Field with Validation
            var emailText by remember { mutableStateOf("") }
            var emailError by remember { mutableStateOf<String?>(null) }
            ClubConnectTextField(
                value = emailText,
                onValueChange = {
                    emailText = it
                    emailError = if (!it.contains("@") && it.isNotEmpty()) {
                        "Please enter a valid email"
                    } else {
                        null
                    }
                },
                label = "Email",
                hint = "Enter your email",
                errorMessage = emailError,
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Numeric Text Fields
        ComponentSection(title = "Numeric Text Fields") {
            Text(
                text = "Specialized text fields for numeric input with optional min/max values.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Integer Text Field
            var numberText by remember { mutableStateOf("") }
            NumberTextField(
                value = numberText,
                onValueChange = { numberText = it },
                label = "Integer Input",
                hint = "Enter a number (0-100)",
                isInteger = true,
                minValue = 0f,
                maxValue = 100f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Decimal Number Text Field
            var decimalText by remember { mutableStateOf("") }
            NumberTextField(
                value = decimalText,
                onValueChange = { decimalText = it },
                label = "Decimal Input",
                hint = "Enter a decimal number",
                isInteger = false,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Enhanced Text Fields
        ComponentSection(title = "Enhanced Text Fields") {
            Text(
                text = "Text fields with additional features like icons and elevation.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Enhanced LabeledTextField inside Card
            var labeledCardText by remember { mutableStateOf("") }
            LabeledTextField(
                value = labeledCardText,
                onValueChange = { labeledCardText = it },
                label = "Username:",
                hint = "Enter username...",
                elevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Text Field with Icon
            var searchText by remember { mutableStateOf("") }
            ClubConnectTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = "Search",
                hint = "Search for items",
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search, contentDescription = "Search"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}