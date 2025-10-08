package com.kelasxi.aplikasimonitoringkelas.ui.theme

import androidx.compose.ui.unit.dp

// Design System - Spacing Scale
object Spacing {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
    val xxxxl = 40.dp
    val xxxxxl = 48.dp
}

// Design System - Component Dimensions
object Dimensions {
    // Standard heights
    val buttonHeight = 48.dp
    val inputFieldHeight = 56.dp
    val toolbarHeight = 64.dp
    val bottomNavHeight = 80.dp
    
    // Card and surface properties
    val cardElevation = 4.dp
    val elevationSmall = 2.dp
    val cardCornerRadius = 12.dp
    val surfaceCornerRadius = 8.dp
    
    // Icon sizes
    val iconSize = 24.dp
    val iconSizeSmall = 16.dp
    val iconSizeMedium = 24.dp
    val iconSizeLarge = 32.dp
    val iconSizeXLarge = 48.dp
    
    // Layout widths and constraints
    val maxContentWidth = 600.dp
    val minTouchTarget = 48.dp
    val dialogMaxWidth = 560.dp
    
    // Specific component spacing
    val cardPadding = Spacing.lg
    val screenPadding = Spacing.lg
    val sectionSpacing = Spacing.xxl
    val itemSpacing = Spacing.sm
}

// Design System - Animation durations
object AnimationDuration {
    const val short = 200
    const val medium = 300
    const val long = 500
}