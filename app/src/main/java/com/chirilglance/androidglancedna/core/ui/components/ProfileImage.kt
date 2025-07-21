package com.chirilglance.androidglancedna.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.chirilglance.androidglancedna.core.extension.conditional
import com.chirilglance.androidglancedna.core.extension.extractInitials
import com.chirilglance.androidglancedna.core.extension.simpleNoRippleClickable

/**
 * A reusable profile image component that displays either a photo or initials
 * as a fallback.
 *
 * This version accepts separate photoUrl and name parameters, making it more
 * flexible and not tied to any specific data class.
 *
 * @param photoUrl The URL of the photo to display (can be null or empty)
 * @param name The name to use for generating initials when photo is unavailable
 * @param modifier Additional modifiers to apply
 * @param size The size of the avatar (both width and height)
 * @param borderWidth The width of the border around the avatar
 * @param borderColor The color of the avatar border
 * @param backgroundColor The background color when showing initials
 * @param textColor The color of the initials text
 * @param fontSize The font size for the initials
 * @param onClick Optional click handler for the avatar
 */
@Composable
fun ProfileImage(
    photoUrl: String?,
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    textColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    fontSize: TextUnit = 16.sp,
    onClick: (() -> Unit)? = null
) {
    val imageModifier = modifier
        .size(size)
        .clip(CircleShape)
        .conditional(borderWidth > 0.dp) {
            border(borderWidth, borderColor, CircleShape)
        }
        .conditional(onClick != null) {
            simpleNoRippleClickable(onClick = onClick!!)
        }

    if (!photoUrl.isNullOrEmpty()) {
        // User has a photo
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photoUrl)
                .crossfade(true)
                .build(),
            contentDescription = "$name's profile picture",
            contentScale = ContentScale.Crop,
            modifier = imageModifier
        ) {
            when (painter.state) {
                is AsyncImagePainter.State.Loading -> {
                    // Loading placeholder
                    Box(
                        modifier = Modifier
                            .size(size)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(size / 2),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    }
                }
                is AsyncImagePainter.State.Error -> {
                    // Error fallback - show initials
                    DisplayInitials(
                        name = name,
                        size = size,
                        backgroundColor = backgroundColor,
                        textColor = textColor,
                        fontSize = fontSize
                    )
                }
                else -> {
                    // Success - show the image
                    SubcomposeAsyncImageContent()
                }
            }
        }
    } else {
        // No photo - display initials
        DisplayInitials(
            name = name,
            size = size,
            modifier = imageModifier,
            backgroundColor = backgroundColor,
            textColor = textColor,
            fontSize = fontSize
        )
    }
}

/**
 * Helper function to display initials in a circle.
 * Uses the extractInitials() extension function.
 */
@Composable
private fun DisplayInitials(
    name: String,
    size: Dp,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    textColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    fontSize: TextUnit = 16.sp
) {
    val initials = name.extractInitials()

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}