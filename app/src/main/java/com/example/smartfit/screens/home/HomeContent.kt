package com.example.smartfit.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.smartfit.data.database.ActivityLog
import com.example.smartfit.screens.home.components.DailyCalendarGraph
import com.example.smartfit.screens.home.components.StepProgressBar
import com.example.smartfit.ui.designsystem.Alpha
import com.example.smartfit.ui.designsystem.AppCard
import com.example.smartfit.ui.designsystem.AppScaffold
import com.example.smartfit.ui.designsystem.AppTypography
import com.example.smartfit.ui.designsystem.EmptyState
import com.example.smartfit.ui.designsystem.ResponsiveLayout
import com.example.smartfit.ui.designsystem.SectionHeader
import com.example.smartfit.ui.designsystem.Sizing
import com.example.smartfit.ui.designsystem.Spacing
import com.example.smartfit.ui.designsystem.StatCard
import com.example.smartfit.viewmodel.ActivityViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun getGreeting(): String {
    // Get the current moment in time
    val now = Clock.System.now()
    // Convert it to the device's local date and time
    val localTime = now.toLocalDateTime(TimeZone.currentSystemDefault())

    // Determine the greeting based on the hour
    return when (localTime.hour) {
        in 0..11 -> "Good Morning!"
        in 12..17 -> "Good Afternoon!"
        else -> "Good Evening!"
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    activityViewModel: ActivityViewModel
) {
    val activities by activityViewModel.activities.collectAsState()
    val steps by activityViewModel.steps.collectAsState()
    val stepGoal by activityViewModel.stepGoal.collectAsState()
    val calories by activityViewModel.calories.collectAsState()
    val distance by activityViewModel.distance.collectAsState()
    val activeTime by activityViewModel.activeTime.collectAsState()
    val weeklyAvgSteps by activityViewModel.weeklyAvgSteps.collectAsState()
    val averageSpeed by activityViewModel.averageSpeed.collectAsState()
    val dailyStepsLast7Days by activityViewModel.dailyStepsLast7Days.collectAsState()

    val progress = if (stepGoal > 0) steps.toFloat() / stepGoal.toFloat() else 0f
    
//    val currentDate = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()) }

    val currentDate = remember {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val dayOfWeek = now.dayOfWeek.name.lowercase().replaceFirstChar { it.titlecase() }
        val month = now.month.name.lowercase().replaceFirstChar { it.titlecase() }
        val dayOfMonth = now.dayOfMonth
        "$dayOfWeek, $month $dayOfMonth"
    }

    ResponsiveLayout(
        modifier = modifier,
        phoneContent = {
            PhoneHomeLayout(
                activities = activities,
                currentDate = currentDate,
                navController = navController,
                steps = steps,
                goal = stepGoal,
                progress = progress,
                calories = calories,
                distance = distance,
                activeTime = activeTime,
                weeklyAvgSteps = weeklyAvgSteps,
                averageSpeed = averageSpeed,
                dailyStepsLast7Days = dailyStepsLast7Days
            )
        },
        tabletContent = {
            TabletHomeLayout(
                activities = activities,
                currentDate = currentDate,
                navController = navController,
                steps = steps,
                goal = stepGoal,
                progress = progress,
                calories = calories,
                distance = distance,
                activeTime = activeTime,
                weeklyAvgSteps = weeklyAvgSteps,
                averageSpeed = averageSpeed,
                dailyStepsLast7Days = dailyStepsLast7Days
            )
        }
    )
}

@Composable
fun PhoneHomeLayout(
    activities: List<ActivityLog>,
    currentDate: String,
    navController: NavController,
    steps: Int,
    goal: Int,
    progress: Float,
    calories: Int,
    distance: Double,
    activeTime: Int,
    weeklyAvgSteps: Int,
    averageSpeed: Double,
    dailyStepsLast7Days: List<Int>
) {
    AppScaffold(
        topBar = { HomeHeader(currentDate) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            item {
                // Daily Calendar Graph with Progress Bar below
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    DailyCalendarGraph(
                        dailySteps = dailyStepsLast7Days,
                        stepGoal = goal,
                        currentSteps = steps,
                        modifier = Modifier.fillMaxWidth()
                    )
                    StepProgressBar(
                        currentSteps = steps,
                        goalSteps = goal,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            item {
                // Stats Grid - Active Time, Calories, Average Speed
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        StatCard(
                            title = "Active Time",
                            value = "$activeTime min",
                            icon = Icons.Default.Timer,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Calories",
                            value = "$calories kcal",
                            icon = Icons.Default.LocalFireDepartment,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        StatCard(
                            title = "Avg. Speed",
                            value = "${String.format("%.1f", averageSpeed)} km/h",
                            icon = Icons.Default.Speed,
                            modifier = Modifier.weight(1f)
                        )
                        // Placeholder for Calorie Intake
                        AppCard(
                            modifier = Modifier.weight(1f),
                            elevation = 1
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = "Calorie Intake",
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    modifier = Modifier.size(Sizing.iconLarge)
                                )
                                Spacer(modifier = Modifier.height(Spacing.xs))
                                Text(
                                    text = "Calorie Intake",
                                    style = AppTypography.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                                )
                                Text(
                                    text = "Coming Soon",
                                    style = AppTypography.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.low)
                                )
                            }
                        }
                    }
                }
            }
            item {
                SectionHeader(title = "Recent Activities", actionText = "View All") {
                    // TODO: Navigate to full activity list
                }
                Spacer(modifier = Modifier.height(Spacing.sm))
                RecentActivities(activities = activities.take(3))
            }
        }
    }
}

@Composable
fun TabletHomeLayout(
    activities: List<ActivityLog>,
    currentDate: String,
    navController: NavController,
    steps: Int,
    goal: Int,
    progress: Float,
    calories: Int,
    distance: Double,
    activeTime: Int,
    weeklyAvgSteps: Int,
    averageSpeed: Double,
    dailyStepsLast7Days: List<Int>
) {
    AppScaffold(
        topBar = { TabletHeader(currentDate) }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    DailyCalendarGraph(
                        dailySteps = dailyStepsLast7Days,
                        stepGoal = goal,
                        currentSteps = steps,
                        modifier = Modifier.fillMaxWidth()
                    )
                    StepProgressBar(
                        currentSteps = steps,
                        goalSteps = goal,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        StatCard(
                            title = "Active Time",
                            value = "$activeTime min",
                            icon = Icons.Default.Timer,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Calories",
                            value = "$calories kcal",
                            icon = Icons.Default.LocalFireDepartment,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        StatCard(
                            title = "Avg. Speed",
                            value = "${String.format("%.1f", averageSpeed)} km/h",
                            icon = Icons.Default.Speed,
                            modifier = Modifier.weight(1f)
                        )
                        AppCard(
                            modifier = Modifier.weight(1f),
                            elevation = 1
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = "Calorie Intake",
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    modifier = Modifier.size(Sizing.iconLarge)
                                )
                                Spacer(modifier = Modifier.height(Spacing.xs))
                                Text(
                                    text = "Calorie Intake",
                                    style = AppTypography.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                                )
                                Text(
                                    text = "Coming Soon",
                                    style = AppTypography.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.low)
                                )
                            }
                        }
                    }
                }
                SectionHeader(title = "Recent Activities", actionText = "View All") {
                     // TODO: Navigate to full activity list
                }
                Spacer(modifier = Modifier.height(Spacing.sm))
                RecentActivities(activities = activities.take(5))
            }
        }
    }
}


@Composable
fun HomeHeader(currentDate: String) {
    // Get the greeting only once
    val greeting = remember { getGreeting() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg)
    ) {
        Text(
            // Use the dynamic greeting here
            text = greeting,
            style = AppTypography.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = currentDate,
            style = AppTypography.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = Alpha.medium)
        )
    }
}

@Composable
fun TabletHeader(currentDate: String) {
    // Get the greeting only once
    val greeting = remember { getGreeting() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.lg)
    ) {
        Text(
            // Use the dynamic greeting for the tablet header as well
            text = greeting,
            style = AppTypography.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = currentDate,
            style = AppTypography.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = Alpha.medium)
        )
    }
}

@Composable
fun RecentActivities(activities: List<ActivityLog>) {
    if (activities.isEmpty()) {
        EmptyState(
            title = "No Recent Activities",
            description = "Go for a walk or run to see your activities here.",
            icon = Icons.Default.FitnessCenter
        )
    } else {
        AppCard(elevation = 1) {
            activities.forEachIndexed { index, activity ->
                RecentActivityItem(activity)
                if (index < activities.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                }
            }
        }
    }
}

@Composable
fun RecentActivityItem(activity: ActivityLog) {
    val timeFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                contentDescription = activity.activityType,
                modifier = Modifier.size(Sizing.iconLarge),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(Spacing.md))
            Column {
                Text(
                    text = activity.activityType,
                    style = AppTypography.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
                Text(
                    text = "${activity.duration} min  •  ${timeFormat.format(Date(activity.date))}",
                    style = AppTypography.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                )
            }
        }
        Text(
            text = "${activity.calories} kcal",
            style = AppTypography.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

