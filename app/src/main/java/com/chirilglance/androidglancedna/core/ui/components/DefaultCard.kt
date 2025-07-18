package com.chirilglance.androidglancedna.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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

/**
 * A highly flexible card component for content presentation in ClubConnect application.
 * This component uses composable slots for title, subtitle, and description, allowing
 * complete customization of content and layout.
 *
 * @param modifier The modifier to be applied to the card
 * @param titleContent Required composable for the card's title area
 * @param subtitleContent Optional composable for content below the title
 * @param descriptionContent Optional composable for descriptive content
 * @param actions Optional composable for action buttons or interactive elements
 * @param elevation The elevation (shadow) of the card in dp
 * @param containerColor The background color of the card
 * @param contentColor The primary color for the content (text)
 * @param onClick Optional callback to handle card click events
 * @param headerContent Optional composable for displaying content at the top of the card
 * @param leadingIcon Optional composable for displaying an icon before the title
 * @param contentPadding Padding applied to the card's content
 * @param contentSpacing The spacing between different content sections
 */
@Composable
fun DefaultCard(
    modifier: Modifier = Modifier,
    titleContent: @Composable () -> Unit,
    subtitleContent: @Composable (() -> Unit)? = null,
    descriptionContent: @Composable (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
    elevation: Dp = 2.dp,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null,
    headerContent: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    contentSpacing: Dp = 8.dp
) {
    val cardModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Card(
        modifier = cardModifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            // Header content if provided
            headerContent?.invoke()

            // Title with leading icon if provided
            if (leadingIcon != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.size(24.dp)) {
                        leadingIcon()
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        titleContent()
                    }
                }
            } else {
                // Title without icon
                titleContent()
            }

            // Subtitle if provided
            subtitleContent?.let {
                Box(modifier = Modifier.padding(top = contentSpacing)) {
                    it()
                }
            }

            // Description if provided
            descriptionContent?.let {
                Box(modifier = Modifier.padding(top = contentSpacing)) {
                    it()
                }
            }

            // Actions if provided
            actions?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = contentSpacing * 2)
                ) {
                    it()
                }
            }
        }
    }
}

/**
 * Helper functions to create common text content for ClubConnectCard
 */
object ClubConnectCardDefaults {
    /**
     * Creates a standard title for use in ClubConnectCard
     *
     * @param text The title text
     * @param style The text style to apply
     * @param color The text color
     */
    @Composable
    fun Title(
        text: String,
        style: TextStyle = MaterialTheme.typography.titleLarge,
        color: Color = MaterialTheme.colorScheme.onSurface
    ) = @Composable {
        Text(
            text = text,
            style = style,
            color = color
        )
    }

    /**
     * Creates a standard subtitle for use in ClubConnectCard
     *
     * @param text The subtitle text
     * @param style The text style to apply
     * @param color The text color
     * @param alpha The opacity level for the text (0.0-1.0)
     */
    @Composable
    fun Subtitle(
        text: String,
        style: TextStyle = MaterialTheme.typography.titleMedium,
        color: Color = MaterialTheme.colorScheme.onSurface,
        alpha: Float = 0.8f
    ) = @Composable {
        Text(
            text = text,
            style = style,
            color = color.copy(alpha = alpha)
        )
    }

    /**
     * Creates a standard description for use in ClubConnectCard
     *
     * @param text The description text
     * @param style The text style to apply
     * @param color The text color
     * @param alpha The opacity level for the text (0.0-1.0)
     */
    @Composable
    fun Description(
        text: String,
        style: TextStyle = MaterialTheme.typography.bodyMedium,
        color: Color = MaterialTheme.colorScheme.onSurface,
        alpha: Float = 0.7f
    ) = @Composable {
        Text(
            text = text,
            style = style,
            color = color.copy(alpha = alpha)
        )
    }
}
