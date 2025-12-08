package com.example.smartfit.screens.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartfit.data.database.ActivityLog
import com.example.smartfit.screens.home.components.DailyCalendarGraph
import com.example.smartfit.screens.home.components.StepProgressBar
import com.example.smartfit.ui.designsystem.*
import com.example.smartfit.viewmodel.ActivityViewModel
import java.text.SimpleDateFormat
import java.util.*

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
    
    val currentDate = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()) }

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
            contentPadding = PaddingValues(horizontal = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            item {
                // Daily Calendar Graph with Progress Bar below
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg)
    ) {
        Text(
            text = "Good Morning!",
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.lg)
    ) {
        Text(
            text = "Welcome Back",
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
fun StepTrackerCircle(steps: Int, goal: Int, progress: Float) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(Spacing.xl),
        contentAlignment = Alignment.Center
    ) {
        val strokeWidth = 20.dp
        val primaryColor = MaterialTheme.colorScheme.primary
        val surfaceColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)

        Canvas(modifier = Modifier.matchParentSize()) {
            drawArc(
                color = surfaceColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx())
            )
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = 360 * animatedProgress.value,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                contentDescription = "Steps walked today: $steps out of $goal steps goal",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Sizing.iconXLarge)
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "$steps",
                style = AppTypography.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (steps >= goal) "Goal reached! +${steps - goal}" else "of $goal steps",
                style = AppTypography.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = Alpha.medium)
            )
        }
    }
}

@Composable
fun StatsGrid(
    calories: Int,
    distance: Double,
    activeTime: Int,
    weeklyAvgSteps: Int,
    averageSpeed: Double
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            StatCard(
                title = "Calories",
                value = "$calories kcal",
                icon = Icons.Default.LocalFireDepartment,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Distance",
                value = "${String.format("%.2f", distance)} km",
                icon = Icons.Default.Map,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
             StatCard(
                title = "Active Time",
                value = "$activeTime min",
                icon = Icons.Default.Timer,
                modifier = Modifier.weight(1f)
            )
             StatCard(
                title = "Avg. Speed",
                value = "${String.format("%.1f", averageSpeed)} km/h",
                icon = Icons.Default.Speed,
                modifier = Modifier.weight(1f)
            )
        }
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

