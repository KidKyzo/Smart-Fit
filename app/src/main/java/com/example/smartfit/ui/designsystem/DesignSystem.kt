package com.example.smartfit.ui.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Centralized design system for SmartFit app
 * Contains all design tokens for consistent UI across the app
 */

// Spacing System
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
    val xxxl = 64.dp
}

// Sizing System
object Sizing {
    val iconSmall = 16.dp
    val iconMedium = 24.dp
    val iconLarge = 32.dp
    val iconXLarge = 48.dp
    val iconXXLarge = 64.dp
    
    val buttonHeight = 48.dp
    val buttonHeightSmall = 36.dp
    val buttonHeightLarge = 56.dp
    
    val cardElevation = 4.dp
    val cardElevationLarge = 8.dp
    
    val profileImageSize = 120.dp
    val profileImageSizeLarge = 150.dp
    
    val quickActionCardSize = 120.dp
}

// Typography System
object AppTypography {
    val typography = Typography(
        displayLarge = TextStyle(
            fontSize = 57.sp,
            lineHeight = 64.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontSize = 45.sp,
            lineHeight = 52.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontSize = 36.sp,
            lineHeight = 44.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontSize = 32.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontSize = 28.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontSize = 22.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.1.sp
        ),
        titleSmall = TextStyle(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontSize = 11.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp
        )
    )
}

// Shape System - Granular corner radius categories
object Shapes {
    // Primary named sizes
    val xs = RoundedCornerShape(4.dp)       // Extra small - subtle rounding
    val sm = RoundedCornerShape(8.dp)       // Small - text fields, small cards
    val md = RoundedCornerShape(12.dp)      // Medium - standard cards, buttons
    val lg = RoundedCornerShape(16.dp)      // Large - prominent cards
    val xl = RoundedCornerShape(20.dp)      // Extra large - featured elements
    val xxl = RoundedCornerShape(24.dp)     // 2x Extra large - hero cards
    val circle = RoundedCornerShape(50)     // Circle/pill shape
    
    // Backward compatibility aliases
    val small = sm
    val medium = md
    val large = lg
    val xLarge = xl
}

// Animation Durations
object AnimationDuration {
    val fast = 150
    val medium = 300
    val slow = 500
}

// Elevation
object Elevation {
    val none = 0.dp
    val small = 2.dp
    val medium = 4.dp
    val large = 8.dp
    val xLarge = 16.dp
}

// Border Width
object BorderWidth {
    val thin = 1.dp
    val medium = 2.dp
    val thick = 3.dp
}

// Alpha Values
object Alpha {
    val transparent = 0f
    val low = 0.1f
    val medium = 0.3f
    val high = 0.6f
    val opaque = 1f
}

// Screen Breakpoints for Responsive Design
object Breakpoints {
    val phone = 600.dp
    val tablet = 840.dp
    val desktop = 1200.dp
}