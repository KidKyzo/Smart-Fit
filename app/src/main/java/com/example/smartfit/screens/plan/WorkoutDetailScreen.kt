package com.example.smartfit.screens.plan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartfit.ui.designsystem.*
import com.example.smartfit.viewmodel.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    navController: NavController,
    viewModel: ExerciseViewModel
) {
    val exercise by viewModel.selectedExercise.collectAsState()

    if (exercise == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Exercise not selected")
        }
        return
    }

    AppScaffold(
        topBar = {
            CompactTopAppBar(
                title = {
                    Text(
                        exercise!!.name.replaceFirstChar { it.uppercase() },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
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
            contentPadding = PaddingValues(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // 1. Image/Icon Card (Matching FoodDetailScreen design)
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
                            contentDescription = exercise!!.name,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // 2. Title Section
            item {
                Column {
                    Text(
                        text = exercise!!.name.replaceFirstChar { it.uppercase() },
                        style = AppTypography.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = "Target Muscle: ${exercise!!.muscle?.replaceFirstChar { it.uppercase() } ?: "General"}",
                        style = AppTypography.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                    )
                }
            }

            // 3. Info Card (Matching Nutritional Information design)
            item {
                AppCard(elevation = 1) {
                    Column {
                        Text(
                            text = "Exercise Details",
                            style = AppTypography.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(Spacing.md))

                        DetailsRow("Type", exercise!!.type ?: "Strength")
                        HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                        DetailsRow("Difficulty", exercise!!.difficulty ?: "Beginner")
                        HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                        DetailsRow("Equipment", exercise!!.equipment ?: "None")
                    }
                }
            }

            // 4. Instructions/Description Card
            item {
                AppCard(elevation = 1) {
                    Column {
                        Text(
                            text = "Instructions",
                            style = AppTypography.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = exercise!!.instructions ?: "No instructions available.",
                            style = AppTypography.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.high)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = AppTypography.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
        )
        Text(
            text = value.replaceFirstChar { it.uppercase() },
            style = AppTypography.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}