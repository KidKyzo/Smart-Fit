package com.example.smartfit.screens.activity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartfit.data.database.ActivityLog
import com.example.smartfit.ui.designsystem.*
import com.example.smartfit.viewmodel.ActivityViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogActivity(
    viewModel: ActivityViewModel,
    onBack: () -> Unit
) {
    val activities by viewModel.activities.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val lastAddedActivity by viewModel.lastAddedActivity.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Show the summary dialog when a new activity is logged
    lastAddedActivity?.let { activity ->
        FinishTrackingScreen(
            steps = activity.steps ?: 0,
            distance = activity.distance,
            calories = activity.calories,
            onDone = {
                viewModel.clearLastAddedActivity()
            }
        )
    }

    AppScaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity Log") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Activity")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    LoadingState(
                        modifier = Modifier.fillMaxSize(),
                        message = "Loading activities..."
                    )
                }
                activities.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.fillMaxSize(),
                        title = "No activities logged yet",
                        description = "Start tracking your fitness journey by adding your first activity.",
                        icon = Icons.Default.FitnessCenter,
                        actionText = "Add Activity",
                        onActionClick = { showAddDialog = true }
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(activities) { activity ->
                            ActivityCard(
                                activity = activity,
                                onDelete = { viewModel.deleteActivity(activity) },
                                onEdit = { viewModel.selectActivity(activity) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddActivityDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { activity ->
                viewModel.addActivity(activity)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ActivityCard(
    activity: ActivityLog,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 1
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = activity.activityType,
                style = AppTypography.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit activity",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete activity",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Duration: ${activity.duration} min",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Calories: ${activity.calories} kcal",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Distance: %.2f km".format(activity.distance),
                    style = AppTypography.typography.bodyMedium
                )
                activity.steps?.let {
                    Text(
                        text = "Steps: $it",
                        style = AppTypography.typography.bodyMedium
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = dateFormat.format(Date(activity.date)),
                    style = AppTypography.typography.bodySmall
                )
                Text(
                    text = timeFormat.format(Date(activity.date)),
                    style = AppTypography.typography.bodySmall
                )
            }
        }

        if (activity.notes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = activity.notes,
                style = AppTypography.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
            )
        }
    }
}

@Composable
fun AddActivityDialog(
    onDismiss: () -> Unit,
    onAdd: (ActivityLog) -> Unit
) {
    var activityType by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Activity") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                OutlinedTextField(
                    value = activityType,
                    onValueChange = { activityType = it },
                    label = { Text("Activity Type") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (minutes)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Calories") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = distance,
                    onValueChange = { distance = it },
                    label = { Text("Distance (km)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = steps,
                    onValueChange = { steps = it },
                    label = { Text("Steps (optional)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val activity = ActivityLog(
                        activityType = activityType,
                        duration = duration.toIntOrNull() ?: 0,
                        calories = calories.toIntOrNull() ?: 0,
                        distance = distance.toDoubleOrNull() ?: 0.0,
                        steps = steps.toIntOrNull(),
                        date = System.currentTimeMillis(),
                        notes = notes
                    )
                    onAdd(activity)
                },
                enabled = activityType.isNotEmpty() && duration.isNotEmpty() && calories.isNotEmpty()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun FinishTrackingScreen(
    steps: Int,
    distance: Double,
    calories: Int,
    onDone: () -> Unit
) {
    // This could be a full screen or a dialog
    AlertDialog(
        onDismissRequest = onDone,
        title = { Text("Activity Complete!", style = AppTypography.typography.headlineSmall) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Text("Congratulations on finishing your activity!", style = AppTypography.typography.bodyLarge)
                HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                StatRow(title = "Total Steps", value = steps.toString())
                StatRow(title = "Distance", value = "%.2f km".format(distance))
                StatRow(title = "Calories Burned", value = "$calories kcal")
            }
        },
        confirmButton = {
            Button(onClick = onDone) {
                Text("Done")
            }
        }
    )
}

@Composable
private fun StatRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = AppTypography.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium))
        Text(value, style = AppTypography.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
    }
}

@Preview (showSystemUi = true)
@Composable
fun LogActivityPreview() {

}
