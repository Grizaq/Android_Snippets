package com.chirilglance.androidglancedna.ui.theme

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
private val GlanceDarkColorScheme = darkColorScheme(
    primary = ClubConnectGreen,
    onPrimary = WhiteDefault,
    secondary = AccentGreen,
    background = Navy,
    error = ErrorRed
)

private val GlanceLightColorScheme = lightColorScheme(
    primary = ClubConnectGreen,
    onPrimary = Navy,
    secondary = AccentGreen,
    background = WhiteDefault,
    error = ErrorRed
)

@Composable
fun AndroidGlanceDNATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    useGlanceTheme: Boolean = true, // Add option to switch between themes
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useGlanceTheme -> if (darkTheme) GlanceDarkColorScheme else GlanceLightColorScheme
        else -> if (darkTheme) OriginalDarkColorScheme else OriginalLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}