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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.smartfit.ui.designsystem.*
import com.example.smartfit.viewmodel.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    navController: NavController,
    viewModel: ExerciseViewModel
) {
    val exercise by viewModel.selectedExercise.collectAsState()

    // 1. Base URL for images in the GitHub repo
    val imageBaseUrl = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/"

    if (exercise == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Exercise not selected")
        }
        return
    }

    // 2. Construct full URL for the first available image
    val imageUrl = remember(exercise) {
        exercise!!.images?.firstOrNull()?.let { "$imageBaseUrl$it" }
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
            // 1. Image Card
            item {
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp), // Increased height for image
                    elevation = 2
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = exercise!!.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        }
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

                    // Fixed: Use primaryMuscles instead of name
                    val muscleText = exercise!!.primaryMuscles?.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "General"
                    Text(
                        text = "Target Muscle: $muscleText",
                        style = AppTypography.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                    )
                }
            }

            // 3. Info Card
            item {
                AppCard(elevation = 1) {
                    Column {
                        Text(
                            text = "Exercise Details",
                            style = AppTypography.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(Spacing.md))

                        DetailsRow("Type", exercise!!.category ?: "Strength")
                        HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                        DetailsRow("Difficulty", exercise!!.level ?: "Beginner")
                        HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                        DetailsRow("Equipment", exercise!!.equipment ?: "None")
                    }
                }
            }

            // 4. Instructions Card
            item {
                AppCard(elevation = 1) {
                    Column {
                        Text(
                            text = "Instructions",
                            style = AppTypography.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(Spacing.md))

                        // Fixed: Handle List<String> for instructions
                        if (!exercise!!.instructions.isNullOrEmpty()) {
                            exercise!!.instructions!!.forEachIndexed { index, step ->
                                Row(
                                    modifier = Modifier.padding(bottom = Spacing.sm),
                                ) {
                                    Text(
                                        text = "${index + 1}. ",
                                        style = AppTypography.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = step,
                                        style = AppTypography.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.high)
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "No instructions available.",
                                style = AppTypography.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                            )
                        }
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