package com.example.smartfit.screens.plan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.smartfit.data.model.MockFoodData
import com.example.smartfit.data.model.MockWorkoutData
import com.example.smartfit.screens.activity.ActivityCard
import com.example.smartfit.screens.activity.AddActivityDialog
import com.example.smartfit.screens.plan.components.CalorieIntakeSection
import com.example.smartfit.screens.plan.components.WorkoutSection
import com.example.smartfit.ui.designsystem.*
import com.example.smartfit.viewmodel.ActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    navController: NavController,
    viewModel: ActivityViewModel
) {
    val activities by viewModel.activities.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    val workouts = remember { MockWorkoutData.getWorkouts() }
    val foods = remember { MockFoodData.getFoods() }

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
            // Workout Suggestions Section
            item {
                WorkoutSection(
                    workouts = workouts.take(5),
                    onWorkoutClick = { workout ->
                        navController.navigate("workout_detail/${workout.id}")
                    },
                    onViewMoreClick = {
                        navController.navigate("workout_list")
                    }
                )
            }
            
            // Calorie Intake Section
            item {
                CalorieIntakeSection(
                    foods = foods.take(5),
                    onFoodClick = { food ->
                        navController.navigate("food_detail/${food.id}")
                    },
                    onViewMoreClick = {
                        navController.navigate("food_list")
                    }
                )
            }
            
            // Recent Activities Section
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
                                activity = activity,
                                onDelete = { viewModel.deleteActivity(activity) }
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
                viewModel.addActivity(activity)
                showAddDialog = false
            }
        )
    }
}
