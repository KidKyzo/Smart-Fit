package com.example.smartfit.screens.plan.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.smartfit.data.network.dto.ExerciseDto
import com.example.smartfit.ui.designsystem.*

@Composable
fun ExerciseSection(
    exercises: List<ExerciseDto>,
    isLoading: Boolean,
    onExerciseClick: (ExerciseDto) -> Unit,
    onViewMoreClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.padding(horizontal = Spacing.lg)) {
            SectionHeader(
                title = "Workout Suggestion",
                actionText = "View More",
                onActionClick = onViewMoreClick
            )
        }
        Spacer(modifier = Modifier.height(Spacing.sm))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (exercises.isEmpty()) {
            Box(modifier = Modifier.padding(horizontal = Spacing.lg)) {
                Text(
                    text = "No exercises found.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                items(exercises.take(5)) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        onClick = { onExerciseClick(exercise) }
                    )
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: ExerciseDto,
    onClick: () -> Unit
) {
    AppCard(
        modifier = Modifier
            .width(160.dp)
            .height(180.dp) // Ukuran tetap
            .clickable { onClick() },
        elevation = 2
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.sm), // Sedikit padding di sekeliling Box
            contentAlignment = Alignment.Center
        ) {
            // 1. Ikon berada di tengah Box, tetapi kita geser sedikit ke atas
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    // Geser ikon sedikit ke atas dari pusat
                    .align(Alignment.Center)
                    .offset(y = (-16).dp),
                tint = MaterialTheme.colorScheme.primary
            )

            // 2. Column untuk teks diletakkan di bagian bawah Box
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter) // Menempelkan teks ke bawah
                    .fillMaxWidth()
                    // Memberi padding bawah agar teks tidak terlalu mepet ke tepi kartu
                    .padding(bottom = Spacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Teks Nama Latihan
                Text(
                    text = exercise.name.replaceFirstChar { it.uppercase() },
                    // Gunakan style yang lebih besar dan jelas
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Teks Otot
                Text(
                    text = exercise.muscle?.replaceFirstChar { it.uppercase() } ?: "General",
                    // Gunakan style yang lebih kecil untuk kontras
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
