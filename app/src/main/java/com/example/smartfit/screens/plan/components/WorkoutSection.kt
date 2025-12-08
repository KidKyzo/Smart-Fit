package com.example.smartfit.screens.plan.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.smartfit.data.model.WorkoutData
import com.example.smartfit.ui.designsystem.*

@Composable
fun WorkoutSection(
    workouts: List<WorkoutData>,
    onWorkoutClick: (WorkoutData) -> Unit,
    onViewMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm)
    ) {
        // Header with View More button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Workout Suggestions",
                style = AppTypography.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "View More",
                style = AppTypography.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onViewMoreClick() }
            )
        }
        
        Spacer(modifier = Modifier.height(Spacing.md))
        
        // Horizontal scrolling workout cards
        LazyRow(
            contentPadding = PaddingValues(horizontal = Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            items(workouts) { workout ->
                WorkoutCard(
                    workout = workout,
                    onClick = { onWorkoutClick(workout) }
                )
            }
        }
    }
}

@Composable
private fun WorkoutCard(
    workout: WorkoutData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier
            .width(160.dp)
            .height(180.dp)
            .clickable { onClick() },
        elevation = 2
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Placeholder for image/gif
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = workout.title,
                    modifier = Modifier.size(Sizing.iconXLarge),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.sm)
            ) {
                Text(
                    text = workout.title,
                    style = AppTypography.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "${workout.duration} • ${workout.difficulty}",
                    style = AppTypography.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
