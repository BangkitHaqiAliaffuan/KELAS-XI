package com.kelasxi.waveoffood.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun WaveOfFoodTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        WaveOfFoodDarkColors
    } else {
        WaveOfFoodLightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WaveOfFoodTypography,
        content = content
    )
}
