package com.kelasxi.myapplication.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = GreenDeep,
    onPrimary = TextOnPrimary,
    primaryContainer = GreenPale,
    onPrimaryContainer = TextPrimary,
    secondary = GreenLighter,
    onSecondary = TextPrimary,
    secondaryContainer = SurfaceVariant,
    onSecondaryContainer = TextPrimary,
    tertiary = OrangeAccent,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE0B2),
    onTertiaryContainer = OrangeDark,
    background = BackgroundGreen,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = GreenPale,
    outlineVariant = DividerColor,
    error = StatusCancelled,
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenDark80,
    onPrimary = OnGreenDark,
    primaryContainer = GreenPrimary,
    onPrimaryContainer = GreenDark80,
    secondary = GreenDark60,
    onSecondary = OnGreenDark,
    secondaryContainer = GreenDeep,
    onSecondaryContainer = GreenDark80,
    tertiary = OrangeAccent,
    onTertiary = Color.Black,
    background = Color(0xFF1A1A1A),
    onBackground = Color(0xFFE8F5E9),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE8F5E9),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFF81C784),
)

@Composable
fun TrashCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep original for backward compat
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = TrashCareTheme(darkTheme = darkTheme, content = content)