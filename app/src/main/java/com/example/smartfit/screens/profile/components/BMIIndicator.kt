package com.example.smartfit.screens.profile.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartfit.ui.designsystem.Alpha
import com.example.smartfit.ui.designsystem.AppTypography
import com.example.smartfit.ui.designsystem.Spacing

@Composable
fun BMIIndicator(
    bmi: Float,
    modifier: Modifier = Modifier
) {
    val (category, color) = getBMICategory(bmi)
    val progress = calculateBMIProgress(bmi)
    
    val animatedProgress = remember { Animatable(0f) }
    
    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress,
            animationSpec = tween(durationMillis = 1000)
        )
    }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // BMI Value and Category
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "BMI",
                    style = AppTypography.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                )
                Text(
                    text = String.format("%.1f", bmi),
                    style = AppTypography.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            
            Text(
                text = category,
                style = AppTypography.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
        
        Spacer(modifier = Modifier.height(Spacing.md))
        
        // Color-coded Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
        ) {
            // Background segments
            Canvas(modifier = Modifier.fillMaxSize()) {
                val segmentWidth = size.width / 3
                
                // Underweight (Yellow) - Left
                drawRoundRect(
                    color = Color(0xFFFFC107).copy(alpha = 0.3f),
                    topLeft = Offset(0f, 0f),
                    size = Size(segmentWidth, size.height),
                    cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                )
                
                // Normal (Green) - Middle
                drawRoundRect(
                    color = Color(0xFF4CAF50).copy(alpha = 0.3f),
                    topLeft = Offset(segmentWidth, 0f),
                    size = Size(segmentWidth, size.height),
                    cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                )
                
                // Overweight (Red) - Right
                drawRoundRect(
                    color = Color(0xFFF44336).copy(alpha = 0.3f),
                    topLeft = Offset(segmentWidth * 2, 0f),
                    size = Size(segmentWidth, size.height),
                    cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                )
            }
            
            // Indicator marker
            Canvas(modifier = Modifier.fillMaxSize()) {
                val markerPosition = size.width * animatedProgress.value
                val markerWidth = 8.dp.toPx()
                
                drawRoundRect(
                    color = color,
                    topLeft = Offset(markerPosition - markerWidth / 2, 0f),
                    size = Size(markerWidth, size.height),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.xs))
        
        // Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Underweight",
                style = AppTypography.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
            )
            Text(
                text = "Normal",
                style = AppTypography.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
            )
            Text(
                text = "Overweight",
                style = AppTypography.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
            )
        }
    }
}

private fun getBMICategory(bmi: Float): Pair<String, Color> {
    return when {
        bmi < 18.5 -> Pair("Underweight", Color(0xFFFFC107)) // Yellow
        bmi < 25.0 -> Pair("Normal", Color(0xFF4CAF50)) // Green
        else -> Pair("Overweight", Color(0xFFF44336)) // Red
    }
}

private fun calculateBMIProgress(bmi: Float): Float {
    // Map BMI to 0-1 range across the three segments
    // Underweight: 0-0.33 (BMI 15-18.5)
    // Normal: 0.33-0.66 (BMI 18.5-25)
    // Overweight: 0.66-1.0 (BMI 25-35)
    
    return when {
        bmi < 15f -> 0f
        bmi < 18.5f -> {
            // Map 15-18.5 to 0-0.33
            ((bmi - 15f) / 3.5f) * 0.33f
        }
        bmi < 25f -> {
            // Map 18.5-25 to 0.33-0.66
            0.33f + ((bmi - 18.5f) / 6.5f) * 0.33f
        }
        bmi < 35f -> {
            // Map 25-35 to 0.66-1.0
            0.66f + ((bmi - 25f) / 10f) * 0.34f
        }
        else -> 1f
    }.coerceIn(0f, 1f)
}
