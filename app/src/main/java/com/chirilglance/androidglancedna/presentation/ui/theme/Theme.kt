package com.chirilglance.androidglancedna.presentation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Original color schemes (for reference)
private val OriginalDarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val OriginalLightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

// New color schemes based on CC
private val GlanceLightColorScheme = lightColorScheme(
    // Primary colors
    primary = ClubConnectGreen,
    onPrimary = Navy,
    primaryContainer = ClubConnectGreen.copy(alpha = 0.7f),
    onPrimaryContainer = Navy,

    // Secondary colors
    secondary = AccentGreen,
    onSecondary = Navy,
    secondaryContainer = AccentGreen.copy(alpha = 0.7f),
    onSecondaryContainer = Navy,

    // Tertiary colors
    tertiary = Navy,
    onTertiary = WhiteDefault,
    tertiaryContainer = Navy.copy(alpha = 0.1f),
    onTertiaryContainer = Navy,

    // Background & Surface
    background = WhiteDefault,
    onBackground = Navy,
    surface = WhiteDefault,
    onSurface = Navy,
    surfaceVariant = WhiteDefault.copy(alpha = 0.9f),
    onSurfaceVariant = Navy.copy(alpha = 0.8f),
    surfaceTint = ClubConnectGreen.copy(alpha = 0.1f),

    // Error colors
    error = ErrorRed,
    onError = WhiteDefault,
    errorContainer = ErrorRed.copy(alpha = 0.1f),
    onErrorContainer = ErrorRed,

    // Other
    outline = Navy.copy(alpha = 0.3f),
    outlineVariant = Navy.copy(alpha = 0.1f),
    scrim = Navy.copy(alpha = 0.3f),
    inverseSurface = Navy,
    inverseOnSurface = WhiteDefault,
    inversePrimary = ClubConnectGreen
)

// Note: the app is focusing on light color there at the moment, for proper handling, no colors should be used outside the MaterialTheme.colorScheme should be used (or tested in both dark and light mode after adding
// Note2: Design should be consistent with theme handling, having a stable set of colors which remain unchanged for similar scenarios
private val GlanceDarkColorScheme = darkColorScheme(
    // Primary colors
    primary = ClubConnectGreen,
    onPrimary = Navy,
    primaryContainer = ClubConnectGreen.copy(alpha = 0.2f),
    onPrimaryContainer = ClubConnectGreen,

    // Secondary colors
    secondary = AccentGreen,
    onSecondary = WhiteDefault,
    secondaryContainer = AccentGreen.copy(alpha = 0.2f),
    onSecondaryContainer = AccentGreen,

    // Tertiary colors
    tertiary = WhiteDefault,
    onTertiary = Navy,
    tertiaryContainer = WhiteDefault.copy(alpha = 0.1f),
    onTertiaryContainer = WhiteDefault,

    // Background & Surface
    background = Navy,
    onBackground = WhiteDefault,
    surface = Navy.copy(alpha = 0.9f),
    onSurface = WhiteDefault,
    surfaceVariant = Navy.copy(alpha = 0.7f),
    onSurfaceVariant = WhiteDefault.copy(alpha = 0.8f),
    surfaceTint = ClubConnectGreen.copy(alpha = 0.1f),

    // Error colors
    error = ErrorRed,
    onError = WhiteDefault,
    errorContainer = ErrorRed.copy(alpha = 0.1f),
    onErrorContainer = ErrorRed,

    // Other
    outline = WhiteDefault.copy(alpha = 0.3f),
    outlineVariant = WhiteDefault.copy(alpha = 0.1f),
    scrim = Navy.copy(alpha = 0.7f),
    inverseSurface = WhiteDefault,
    inverseOnSurface = Navy,
    inversePrimary = ClubConnectGreen
)

@Composable
fun AndroidGlanceDNATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    useGlanceTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // For single color scheme apps:
        // useGlanceTheme -> if (darkTheme) GlanceLightColorScheme else GlanceLightColorScheme
        useGlanceTheme -> if (darkTheme) GlanceDarkColorScheme else GlanceLightColorScheme
        else -> if (darkTheme) OriginalDarkColorScheme else OriginalLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Keep ClubConnectGreen as status bar color for both themes
            window.statusBarColor = colorScheme.primary.toArgb()

            // In dark mode, we still want dark status bar content (Navy)
            // This is the opposite of the default behavior where dark theme = light status bar content
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}