package com.example.smartfit.screens.plan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartfit.data.model.MockWorkoutData
import com.example.smartfit.data.model.WorkoutData
import com.example.smartfit.ui.designsystem.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutListScreen(
    navController: NavController
) {
    val workouts = remember { MockWorkoutData.getWorkouts() }
    
    AppScaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workouts") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            items(workouts) { workout ->
                WorkoutListItem(
                    workout = workout,
                    onClick = {
                        navController.navigate("workout_detail/${workout.id}")
                    }
                )
            }
        }
    }
}

@Composable
private fun WorkoutListItem(
    workout: WorkoutData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = 1
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image/GIF placeholder
            Box(
                modifier = Modifier
                    .size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = workout.title,
                    modifier = Modifier.size(Sizing.iconXLarge),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // Title and description
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = workout.title,
                    style = AppTypography.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = workout.description,
                    style = AppTypography.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Text(
                        text = workout.duration,
                        style = AppTypography.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "•",
                        style = AppTypography.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                    )
                    Text(
                        text = workout.difficulty,
                        style = AppTypography.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
