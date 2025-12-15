package com.example.smartfit.screens.plan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartfit.Routes
import com.example.smartfit.data.network.dto.ExerciseDto
import com.example.smartfit.ui.designsystem.*
import com.example.smartfit.viewmodel.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutListScreen(
    navController: NavController,
    viewModel: ExerciseViewModel
) {
    val exercises by viewModel.exercises.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedBodyPart by viewModel.selectedBodyPart.collectAsState()

    // API Ninjas valid muscle groups
    val bodyParts = listOf(
        "biceps", "triceps", "chest", "back", "legs",
        "abdominals", "calves", "glutes", "hamstrings",
        "quadriceps", "shoulders", "traps"
    )

    AppScaffold(
        topBar = {
            CompactTopAppBar(
                title = { Text("Exercises") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            // Filter Chips
            LazyRow(
                contentPadding = PaddingValues(Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(bodyParts) { part ->
                    FilterChip(
                        selected = part == selectedBodyPart,
                        onClick = { viewModel.loadExercises(part) },
                        label = { Text(part.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    items(exercises) { exercise ->
                        ExerciseItem(
                            exercise = exercise,
                            onClick = {
                                // 1. Save selected exercise to Shared ViewModel
                                viewModel.selectExercise(exercise)
                                // 2. Navigate without ID
                                navController.navigate(Routes.workoutDetail)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseItem(exercise: ExerciseDto, onClick: () -> Unit) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = 2
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Static Placeholder Icon
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .padding(Spacing.sm),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(Spacing.md))

            Column {
                Text(
                    text = exercise.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = exercise.muscle?.replaceFirstChar { it.uppercase() } ?: "General",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = exercise.difficulty?.replaceFirstChar { it.uppercase() } ?: "Easy",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}