package com.kelasxi.aplikasimonitoringkelas.ui.theme

import androidx.compose.ui.graphics.Color

// SMKN 2 BUDURAN SIDOARJO - Color System
// Modern Emerald/Teal palette for professional education app

// Primary Colors - Emerald Green (Modern & Fresh)
val SMKPrimary = Color(0xFF059669)          // Emerald 600 - main brand color
val SMKPrimaryLight = Color(0xFF34D399)     // Emerald 400 - light variant
val SMKPrimaryDark = Color(0xFF047857)      // Emerald 700 - dark variant
val SMKPrimaryContainer = Color(0xFFD1FAE5) // Emerald 100 - container

val SMKSecondary = Color(0xFF0D9488)        // Teal 600 - secondary color
val SMKSecondaryLight = Color(0xFF2DD4BF)   // Teal 400 - light variant
val SMKSecondaryDark = Color(0xFF0F766E)    // Teal 700 - dark variant
val SMKSecondaryContainer = Color(0xFFCCFBF1) // Teal 100 - container

val SMKAccent = Color(0xFF8B5CF6)           // Violet 500 - accent color
val SMKAccentLight = Color(0xFFA78BFA)      // Violet 400 - light variant
val SMKAccentDark = Color(0xFF7C3AED)       // Violet 600 - dark variant
val SMKAccentContainer = Color(0xFFEDE9FE)  // Violet 100 - container

// Supporting Colors - Clean Modern Palette
val SMKBackground = Color(0xFFF8FAFC)       // Slate 50 - soft background
val SMKSurface = Color(0xFFFFFFFF)          // Pure white surface
val SMKSurfaceVariant = Color(0xFFF1F5F9)   // Slate 100 - subtle surface variant
val SMKOnSurface = Color(0xFF0F172A)        // Slate 900 - primary text
val SMKOnSurfaceVariant = Color(0xFF475569) // Slate 600 - secondary text
val SMKOutline = Color(0xFFCBD5E1)          // Slate 300 - subtle borders
val SMKOutlineVariant = Color(0xFFE2E8F0)   // Slate 200 - light borders

// Status Colors - Semantic meanings
val SMKSuccess = Color(0xFF22C55E)          // Green 500 - success/completed
val SMKSuccessLight = Color(0xFF4ADE80)     // Green 400 - light success
val SMKSuccessContainer = Color(0xFFDCFCE7) // Green 100 - success container

val SMKWarning = Color(0xFFF59E0B)          // Amber 500 - warnings/pending  
val SMKWarningLight = Color(0xFFFBBF24)     // Amber 400 - light warning
val SMKWarningContainer = Color(0xFFFEF3C7) // Amber 100 - warning container

val SMKError = Color(0xFFEF4444)            // Red 500 - errors/urgent
val SMKErrorLight = Color(0xFFF87171)       // Red 400 - light error
val SMKErrorContainer = Color(0xFFFEE2E2)   // Red 100 - error container

val SMKInfo = Color(0xFF0EA5E9)             // Sky 500 - info/general
val SMKInfoLight = Color(0xFF38BDF8)        // Sky 400 - light info
val SMKInfoContainer = Color(0xFFE0F2FE)    // Sky 100 - info container

// Gradient Colors for enhanced visual appeal
val SMKGradientStart = SMKPrimary           // Emerald start
val SMKGradientEnd = SMKSecondary           // Teal end
val SMKGradientMid = Color(0xFF10B981)      // Emerald 500

// Neutral Grays - Slate palette for hierarchy
val NeutralGray50 = Color(0xFFF8FAFC)      // Slate 50 - lightest
val NeutralGray100 = Color(0xFFF1F5F9)     // Slate 100
val NeutralGray200 = Color(0xFFE2E8F0)     // Slate 200
val NeutralGray300 = Color(0xFFCBD5E1)     // Slate 300
val NeutralGray400 = Color(0xFF94A3B8)     // Slate 400
val NeutralGray500 = Color(0xFF64748B)     // Slate 500
val NeutralGray600 = Color(0xFF475569)     // Slate 600
val NeutralGray700 = Color(0xFF334155)     // Slate 700
val NeutralGray800 = Color(0xFF1E293B)     // Slate 800
val NeutralGray900 = Color(0xFF0F172A)     // Slate 900 - darkest

// Legacy compatibility - mapped to new system
val SchoolBlue80 = SMKPrimaryContainer     // Light emerald
val SchoolTeal80 = SMKSecondaryContainer   // Light teal
val SchoolGreen80 = SMKSuccessContainer    // Light green

val SchoolBlue40 = SMKPrimary              // Primary emerald
val SchoolTeal40 = SMKSecondary            // Secondary teal
val SchoolGreen40 = SMKSuccess             // Success green

// Additional semantic colors (legacy)
val SuccessLight = SMKSuccessContainer
val WarningLight = SMKWarningContainer
val ErrorLight = SMKErrorContainer  
val InfoLight = SMKInfoContainer

val SuccessDark = SMKSuccess
val WarningDark = SMKWarning
val ErrorDark = SMKError
val InfoDark = SMKInfo

// Neutral grays (legacy compatibility)
val NeutralGray10 = NeutralGray50          // Surface variant light
val NeutralGray20 = NeutralGray100         // Background light
val NeutralGray90 = NeutralGray800         // On surface dark
val NeutralGray80 = NeutralGray700         // On surface variant dark

// Legacy colors for backward compatibility
val Purple80 = SchoolBlue80
val PurpleGrey80 = SchoolTeal80
val Pink80 = SchoolGreen80

val Purple40 = SchoolBlue40
val PurpleGrey40 = SchoolTeal40
val Pink40 = SchoolGreen40

// Legacy aliases for semantic colors (to maintain compatibility with existing code)
val ErrorRed = SMKError
val SuccessGreen = SMKSuccess
val WarningYellow = SMKWarning
val SMKOnPrimary = Color(0xFFFFFFFF)  // White color for text on primary color