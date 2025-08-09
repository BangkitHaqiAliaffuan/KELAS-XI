package com.kelasxi.waveoffood.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Wave Of Food Brand Colors
val WaveOrange = Color(0xFFFF6B35)
val WaveOrangeLight = Color(0xFFFF8A65)
val WaveOrangeDark = Color(0xFFE65100)

val WaveGreen = Color(0xFF2E7D32)
val WaveGreenLight = Color(0xFF4CAF50)
val WaveGreenDark = Color(0xFF1B5E20)

val WaveGreenContainer = Color(0xFFE8F5E8)
val WaveOrangeContainer = Color(0xFFFFF3E0)

// Neutral Colors
val Gray50 = Color(0xFFFAFAFA)
val Gray100 = Color(0xFFF5F5F5)
val Gray200 = Color(0xFFEEEEEE)
val Gray300 = Color(0xFFE0E0E0)
val Gray400 = Color(0xFFBDBDBD)
val Gray500 = Color(0xFF9E9E9E)
val Gray600 = Color(0xFF757575)
val Gray700 = Color(0xFF616161)
val Gray800 = Color(0xFF424242)
val Gray900 = Color(0xFF212121)

// Status Colors
val Success = Color(0xFF4CAF50)
val Warning = Color(0xFFFF9800)
val Error = Color(0xFFF44336)
val Info = Color(0xFF2196F3)

// Material 3 Light Color Scheme for Wave Of Food
val WaveOfFoodLightColors = lightColorScheme(
    // Primary colors - Wave Orange theme
    primary = WaveOrange,
    onPrimary = Color.White,
    primaryContainer = WaveOrangeContainer,
    onPrimaryContainer = WaveOrangeDark,
    
    // Secondary colors - Wave Green theme
    secondary = WaveGreen,
    onSecondary = Color.White,
    secondaryContainer = WaveGreenContainer,
    onSecondaryContainer = WaveGreenDark,
    
    // Tertiary colors
    tertiary = Gray600,
    onTertiary = Color.White,
    
    // Surface colors
    surface = Color.White,
    onSurface = Gray900,
    surfaceVariant = Gray50,
    onSurfaceVariant = Gray700,
    
    // Background colors
    background = Gray50,
    onBackground = Gray900,
    
    // Error colors
    error = Error,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    // Outline colors
    outline = Gray400,
    outlineVariant = Gray200,
    
    // Surface containers
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Gray50,
    surfaceContainer = Gray100,
    surfaceContainerHigh = Gray200,
    surfaceContainerHighest = Gray300
)

// Material 3 Dark Color Scheme for Wave Of Food
val WaveOfFoodDarkColors = darkColorScheme(
    primary = WaveOrangeLight,
    onPrimary = Color.Black,
    primaryContainer = WaveOrangeDark,
    onPrimaryContainer = WaveOrangeLight,
    
    secondary = WaveGreenLight,
    onSecondary = Color.Black,
    secondaryContainer = WaveGreenDark,
    onSecondaryContainer = WaveGreenLight,
    
    tertiary = Gray400,
    onTertiary = Color.Black,
    
    surface = Gray900,
    onSurface = Color.White,
    surfaceVariant = Gray800,
    onSurfaceVariant = Gray300,
    
    background = Gray900,
    onBackground = Color.White,
    
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    
    outline = Gray600,
    outlineVariant = Gray700,
    
    surfaceContainerLowest = Color.Black,
    surfaceContainerLow = Gray900,
    surfaceContainer = Gray800,
    surfaceContainerHigh = Gray700,
    surfaceContainerHighest = Gray600
)
