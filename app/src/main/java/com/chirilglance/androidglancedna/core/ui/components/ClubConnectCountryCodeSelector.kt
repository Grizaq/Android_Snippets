package com.chirilglance.androidglancedna.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chirilglance.androidglancedna.domain.models.CountryCode
import com.chirilglance.androidglancedna.domain.utils.CountryCodeProvider

/**
 * A dropdown selector component for country codes with flag emoji display.
 *
 * @param selectedCountry The currently selected country code
 * @param onCountrySelected Callback when a country is selected
 * @param modifier Modifier to be applied to the component
 * @param elevation Shadow elevation of the card
 * @param shape Shape of the card
 * @param backgroundColor Background color of the card
 * @param dropdownWidth Width of the dropdown menu
 * @param dropdownMaxHeight Maximum height of the dropdown menu
 * @param showFlagEmoji Whether to show the flag emoji next to the country code
 * @param hideKeyboardOnSelect Whether to hide the keyboard when selecting a country
 */
@Composable
fun ClubConnectCountryCodeSelector(
    selectedCountry: CountryCode,
    onCountrySelected: (CountryCode) -> Unit,
    modifier: Modifier = Modifier,
    elevation: Dp = 4.dp,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    dropdownWidth: Dp = 240.dp,
    dropdownMaxHeight: Dp = 350.dp,
    showFlagEmoji: Boolean = true,
    hideKeyboardOnSelect: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier) {
        // Selected country display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable {
                    if (hideKeyboardOnSelect) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                    expanded = true
                },
            shape = shape,
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Country flag emoji and code
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Flag emoji if enabled
                    if (showFlagEmoji) {
                        Text(
                            text = CountryCodeProvider.codeToEmoji(selectedCountry.code),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    // Dial code
                    Text(
                        text = selectedCountry.dialCode,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Dropdown arrow
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Country"
                )
            }
        }

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(dropdownWidth)
                .heightIn(max = dropdownMaxHeight)
        ) {
            CountryCodeProvider.getAllCountryCodes().forEach { country ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (showFlagEmoji) {
                                Text(
                                    text = CountryCodeProvider.codeToEmoji(country.code),
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = "${country.name} (${country.dialCode})",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    onClick = {
                        onCountrySelected(country)
                        expanded = false
                    }
                )
            }
        }
    }
}