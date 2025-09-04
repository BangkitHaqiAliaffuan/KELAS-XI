package com.kelasxi.waveoffood.ui.theme

import androidx.compose.ui.unit.dp

// Spacing & Dimensions
object Spacing {
    val tiny = 4.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val xLarge = 32.dp
    val xxLarge = 48.dp
    val xxxLarge = 64.dp
}

// Corner Radius
object CornerRadius {
    val small = 8.dp
    val medium = 12.dp
    val large = 16.dp
    val xLarge = 20.dp
    val xxLarge = 24.dp
    val xxxLarge = 32.dp
}

// Elevation
object Elevation {
    val none = 0.dp
    val small = 2.dp
    val medium = 4.dp
    val large = 8.dp
    val xLarge = 12.dp
    val xxLarge = 16.dp
}

// Component Sizes
object ComponentSize {
    val buttonHeight = 56.dp
    val buttonHeightSmall = 40.dp
    val buttonHeightMedium = 48.dp
    
    val iconSize = 24.dp
    val iconSizeSmall = 16.dp
    val iconSizeLarge = 32.dp
    val iconSizeXLarge = 48.dp
    
    val avatarSize = 40.dp
    val avatarSizeLarge = 64.dp
    val avatarSizeXLarge = 80.dp
    
    val cardHeight = 180.dp
    val cardHeightSmall = 120.dp
    val cardHeightLarge = 240.dp
    
    val inputFieldHeight = 56.dp
    val searchBarHeight = 48.dp
    
    val bottomBarHeight = 80.dp
    val topBarHeight = 56.dp
}

// Aspect Ratios
object AspectRatio {
    const val square = 1f
    const val landscape = 16f / 9f
    const val portrait = 9f / 16f
    const val food = 4f / 3f
    const val banner = 3f / 1f
}

// Animation Durations (in milliseconds)
object AnimationDuration {
    const val fast = 150
    const val normal = 300
    const val slow = 500
    const val extraSlow = 800
}

// Z-Index values for layering
object ZIndex {
    const val background = 0f
    const val content = 1f
    const val overlay = 2f
    const val modal = 3f
    const val tooltip = 4f
    const val dropdown = 5f
}

// Touch Target Sizes (Material Design minimum)
object TouchTarget {
    val minimum = 48.dp
    val recommended = 56.dp
}

// Grid Layout
object Grid {
    val columns = 2
    val spacing = Spacing.medium
    val padding = Spacing.medium
}
