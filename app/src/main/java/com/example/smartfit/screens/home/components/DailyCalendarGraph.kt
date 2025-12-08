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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DailyCalendarGraph(
    dailySteps: List<Int>, // Last 7 days of step counts
    stepGoal: Int,
    currentSteps: Int,
    modifier: Modifier = Modifier
) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    
    // Get current day of week (1 = Sunday, 2 = Monday, ..., 7 = Saturday)
    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    
    // Convert to Monday-based index (0 = Monday, 1 = Tuesday, ..., 6 = Sunday)
    val todayIndex = when (dayOfWeek) {
        Calendar.SUNDAY -> 6      // Sunday is last day (index 6)
        Calendar.MONDAY -> 0      // Monday is first day (index 0)
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        else -> 0
    }
    
    // Ensure we have 7 days of data
    val stepData = dailySteps.takeLast(7).let { data ->
        if (data.size < 7) {
            List(7 - data.size) { 0 } + data
        } else {
            data
        }
    }
    
    // Animation for bars
    val animatedValues = stepData.map { remember { Animatable(0f) } }
    
    LaunchedEffect(stepData) {
        animatedValues.forEachIndexed { index, animatable ->
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 500,
                    delayMillis = index * 50
                )
            )
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Daily Activity",
                style = AppTypography.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date()),
                style = AppTypography.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
            )
        }
        
        Spacer(modifier = Modifier.height(Spacing.sm))
        
        // Graph
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            stepData.forEachIndexed { index, steps ->
                DayBar(
                    day = days[index],
                    steps = steps,
                    maxSteps = stepGoal,
                    isToday = index == todayIndex,
                    animatedProgress = animatedValues[index].value,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.sm))
        
        // Current steps display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$currentSteps",
                style = AppTypography.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(
                text = "steps today",
                style = AppTypography.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
            )
        }
    }
}

@Composable
private fun DayBar(
    day: String,
    steps: Int,
    maxSteps: Int,
    isToday: Boolean,
    animatedProgress: Float,
    modifier: Modifier = Modifier
) {
    val barHeight = if (maxSteps > 0) {
        ((steps.toFloat() / maxSteps.toFloat()).coerceIn(0f, 1f) * 140.dp.value).dp
    } else {
        0.dp
    }
    
    val barColor = if (isToday) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    }
    
    Column(
        modifier = modifier.padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bar
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(140.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Canvas(
                modifier = Modifier
                    .width(32.dp)
                    .height(barHeight * animatedProgress)
            ) {
                drawRoundRect(
                    color = barColor,
                    size = Size(size.width, size.height),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.xs))
        
        // Day label
        Text(
            text = day,
            style = AppTypography.typography.labelSmall,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            color = if (isToday) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
            }
        )
    }
}
