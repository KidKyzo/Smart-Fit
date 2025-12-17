package com.example.smartfit.screens.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    // State for undo functionality
    val snackbarHostState = remember { SnackbarHostState() }
    var deletedItem by remember { mutableStateOf<ActivityLog?>(null) }

    // Handle Undo Snackbar logic
    LaunchedEffect(deletedItem) {
        deletedItem?.let { item ->
            val result = snackbarHostState.showSnackbar(
                message = "${item.activityType} removed",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )

            if (result == SnackbarResult.ActionPerformed) {
                viewModel.addActivity(item) // Re-add the item
            }
            deletedItem = null
        }
    }

    // Group activities by day
    val groupedActivities = remember(activities) {
        activities.sortedByDescending { it.date }.groupBy { log ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = log.date
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }
    }

    AppScaffold(
        topBar = {
            CompactTopAppBar(
                title = { Text("Activity Log") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        groupedActivities.forEach { (dayTimestamp, activitiesForDay) ->
                            // Day Header
                            item {
                                ActivityDateHeader(
                                    dayTimestamp = dayTimestamp,
                                    totalCalories = activitiesForDay.sumOf { it.calories },
                                    totalDuration = activitiesForDay.sumOf { it.duration }
                                )
                            }

                            // Swipe to Delete Items
                            items(
                                items = activitiesForDay,
                                key = { it.id }
                            ) { activity ->
                                SwipeToDeleteActivityItem(
                                    activity = activity,
                                    onDelete = {
                                        deletedItem = activity
                                        viewModel.deleteActivity(activity)
                                    }
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(Spacing.xs))
                            }
                        }
                    }
                }
            }

            // SnackbarHost placed explicitly at the bottom of the content Box
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
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
fun ActivityDateHeader(
    dayTimestamp: Long,
    totalCalories: Int,
    totalDuration: Int
) {
    val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
    val dateString = dateFormat.format(Date(dayTimestamp))

    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val displayDate = when (dayTimestamp) {
        today -> "Today"
        today - 86400000 -> "Yesterday"
        else -> dateString
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = displayDate,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${totalDuration}m",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                Icon(
                    Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$totalCalories",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(top = Spacing.xs))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteActivityItem(
    activity: ActivityLog,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        },
        positionalThreshold = { distance -> distance * 0.5f }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier.padding(horizontal = Spacing.lg),
        backgroundContent = {
            val progress = dismissState.progress
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.error.copy(alpha = progress * 0.3f)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onError.copy(alpha = progress),
                    modifier = Modifier
                        .padding(horizontal = Spacing.lg)
                        .size(Sizing.iconLarge)
                )
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true
    ) {
        ActivityCard(activity = activity)
    }
}

@Composable
fun ActivityCard(
    activity: ActivityLog
) {
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
            Column {
                Text(
                    text = activity.activityType,
                    style = AppTypography.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = timeFormat.format(Date(activity.date)),
                    style = AppTypography.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                )
            }

            // Delete button removed here as requested
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    icon = Icons.Default.Timer,
                    value = "${activity.duration} min",
                    color = MaterialTheme.colorScheme.primary
                )
                StatItem(
                    icon = Icons.Default.LocalFireDepartment,
                    value = "${activity.calories} kcal",
                    color = MaterialTheme.colorScheme.tertiary
                )
                StatItem(
                    icon = Icons.Default.Map,
                    value = "%.2f km".format(activity.distance),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        activity.steps?.let {
            if (it > 0) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.DirectionsWalk,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$it steps",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
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
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityDialog(
    onDismiss: () -> Unit,
    onAdd: (ActivityLog) -> Unit
) {
    val activityTypes = listOf("Running", "Jogging", "Cycling", "Hiking")
    var selectedActivityType by remember { mutableStateOf(activityTypes[0]) }
    var expanded by remember { mutableStateOf(false) }
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
                // Activity Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedActivityType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Activity Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        activityTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedActivityType = type
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
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
                        userId = 0, // Placeholder - ViewModel will set correct userId
                        activityType = selectedActivityType,
                        duration = duration.toIntOrNull() ?: 0,
                        calories = calories.toIntOrNull() ?: 0,
                        distance = distance.toDoubleOrNull() ?: 0.0,
                        steps = steps.toIntOrNull(),
                        date = System.currentTimeMillis(),
                        notes = notes
                    )
                    onAdd(activity)
                },
                enabled = duration.isNotEmpty() && calories.isNotEmpty()
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

@Preview (showSystemUi = true)
@Composable
fun LogActivityPreview() {
    // Preview content
}