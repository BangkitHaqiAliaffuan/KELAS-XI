package com.kelasxi.aplikasimonitoringkelas.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val SchoolDarkColorScheme = darkColorScheme(
    primary = SchoolBlue80,
    secondary = SchoolTeal80,
    tertiary = SchoolGreen80,
    background = NeutralGray90,
    surface = NeutralGray80,
    onPrimary = NeutralGray10,
    onSecondary = NeutralGray10,
    onTertiary = NeutralGray10,
    onBackground = NeutralGray10,
    onSurface = NeutralGray10,
    primaryContainer = SchoolBlue40,
    secondaryContainer = SchoolTeal40,
    onPrimaryContainer = NeutralGray10,
    onSecondaryContainer = NeutralGray10
)

private val SchoolLightColorScheme = lightColorScheme(
    primary = SchoolBlue40,
    secondary = SchoolTeal40,
    tertiary = SchoolGreen40,
    background = NeutralGray10,
    surface = androidx.compose.ui.graphics.Color.White,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    onBackground = NeutralGray90,
    onSurface = NeutralGray90,
    primaryContainer = SchoolBlue80,
    secondaryContainer = SchoolTeal80,
    onPrimaryContainer = NeutralGray90,
    onSecondaryContainer = NeutralGray90,
    surfaceVariant = NeutralGray20,
    onSurfaceVariant = NeutralGray80,
    outline = NeutralGray80,
    outlineVariant = SchoolTeal80
)

// Legacy schemes for backward compatibility
private val DarkColorScheme = SchoolDarkColorScheme
private val LightColorScheme = SchoolLightColorScheme

@Composable
fun AplikasiMonitoringKelasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic color untuk konsistensi dengan branding sekolah
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> SchoolDarkColorScheme
        else -> SchoolLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}