package com.example.smartfit.screens.plan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.smartfit.Routes
import com.example.smartfit.screens.activity.ActivityCard
import com.example.smartfit.screens.activity.AddActivityDialog
import com.example.smartfit.screens.plan.components.CalorieIntakeSection
import com.example.smartfit.screens.plan.components.ExerciseSection // Import the new section
import com.example.smartfit.ui.designsystem.*
import com.example.smartfit.viewmodel.ActivityViewModel
import com.example.smartfit.viewmodel.ExerciseViewModel
import com.example.smartfit.viewmodel.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    navController: NavController,
    activityViewModel: ActivityViewModel,
    foodViewModel: FoodViewModel,
    exerciseViewModel: ExerciseViewModel
) {
    val activities by activityViewModel.activities.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    AppScaffold(
        topBar = {
            CompactTopAppBar(
                title = { Text("Activity Plan") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Activity"
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            item {
                ExerciseSection(
                    onNavigateToList = {
                        navController.navigate(Routes.workoutList)
                    }
                )
            }

            item {
                CalorieIntakeSection(
                    onNavigateToList = {
                        navController.navigate(Routes.foodList)
                    }
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg)
                ) {
                    SectionHeader(
                        title = "Activity Log",
                        actionText = "View All",
                        onActionClick = { navController.navigate("log_activity") }
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))

                    if (activities.isEmpty()) {
                        EmptyState(
                            title = "No activities logged yet",
                            description = "Start tracking your fitness journey by adding your first activity.",
                            icon = Icons.Default.Add,
                            actionText = "Add Activity",
                            onActionClick = { showAddDialog = true }
                        )
                    } else {
                        activities.take(3).forEach { activity ->
                            ActivityCard(
                                activity = activity
                            )
                            Spacer(modifier = Modifier.height(Spacing.md))
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
                activityViewModel.addActivity(activity)
                showAddDialog = false
            }
        )
    }
}