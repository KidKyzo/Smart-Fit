package com.example.smartfit.screens.plan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.smartfit.ui.designsystem.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    navController: NavController,
    workoutId: Int
) {
    val workout = remember { MockWorkoutData.getWorkoutById(workoutId) }
    
    if (workout == null) {
        // Handle workout not found
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Workout not found")
        }
        return
    }
    
    AppScaffold(
        topBar = {
            TopAppBar(
                title = { Text(workout.title) },
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
            // Image/GIF section
            item {
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    elevation = 2
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = workout.title,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Workout info
            item {
                Column {
                    Text(
                        text = workout.title,
                        style = AppTypography.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        AppCard(elevation = 1) {
                            Text(
                                text = workout.duration,
                                style = AppTypography.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        AppCard(elevation = 1) {
                            Text(
                                text = workout.difficulty,
                                style = AppTypography.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
            
            // Description
            item {
                AppCard(elevation = 1) {
                    Column {
                        Text(
                            text = "Description",
                            style = AppTypography.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = workout.description,
                            style = AppTypography.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.high)
                        )
                    }
                }
            }
            
            // Step-by-step instructions
            item {
                AppCard(elevation = 1) {
                    Column {
                        Text(
                            text = "Instructions",
                            style = AppTypography.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(Spacing.md))
                    }
                }
            }
            
            itemsIndexed(workout.steps) { index, step ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    // Step number
                    Surface(
                        shape = Shapes.circle,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = AppTypography.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // Step description
                    Text(
                        text = step,
                        style = AppTypography.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
