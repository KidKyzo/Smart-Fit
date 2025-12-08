package com.example.smartfit.screens.home.components

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
fun StepProgressBar(
    currentSteps: Int,
    goalSteps: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (goalSteps > 0) {
        (currentSteps.toFloat() / goalSteps.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    
    val percentage = (progress * 100).toInt()
    
    val animatedProgress = remember { Animatable(0f) }
    
    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress,
            animationSpec = tween(durationMillis = 1000)
        )
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Percentage text on left
        Text(
            text = "$percentage%",
            style = AppTypography.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        // Horizontal progress bar in middle
        Box(
            modifier = Modifier
                .weight(1f)
                .height(20.dp)
                .padding(horizontal = Spacing.md)
        ) {
            // Background bar
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = Color.Gray.copy(alpha = 0.15f),
                    size = Size(size.width, size.height),
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                )
            }
            
            // Progress bar (fills from left)
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.value)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        color = Color(0xFF4CAF50), // Green color for progress
                        size = Size(size.width, size.height),
                        cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                    )
                }
            }
        }
        
        // Goal text on right
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "Goal",
                style = AppTypography.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
            )
            Text(
                text = "$goalSteps",
                style = AppTypography.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
