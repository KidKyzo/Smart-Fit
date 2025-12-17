package com.example.smartfit.screens.profile

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartfit.data.database.ActivityLog
import com.example.smartfit.ui.designsystem.*
import com.example.smartfit.ui.designsystem.Shapes
import com.example.smartfit.ui.theme.*
import com.example.smartfit.viewmodel.ActivityViewModel
import com.example.smartfit.viewmodel.FoodViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyReportScreen(
    navController: NavController,
    viewModel: ActivityViewModel,
    foodViewModel: FoodViewModel? = null,
    weekOffset: Int = 0
) {
    var currentWeekOffset by remember { mutableStateOf(weekOffset) }
    var selectedDayIndex by remember { mutableStateOf(getCurrentDayIndex()) }
    
    val weekActivities = viewModel.getActivitiesByWeek(currentWeekOffset)
    val weekDates = getWeekDates(currentWeekOffset)
    val selectedDate = weekDates[selectedDayIndex]
    val dailyActivities = viewModel.getDailyActivities(selectedDate)
    
    // Calculate totals for selected day
    val totalDuration = dailyActivities.sumOf { it.duration }
    val totalCalories = dailyActivities.sumOf { it.calories }
    val totalDistance = dailyActivities.sumOf { it.distance }
    val longestActivity = viewModel.getLongestActivityForDay(selectedDate)
    
    // Get calorie intake for selected day
    var calorieIntake by remember { mutableStateOf(0) }
    LaunchedEffect(selectedDate, foodViewModel) {
        calorieIntake = foodViewModel?.getCaloriesForDate(selectedDate) ?: 0
    }
    
    AppScaffold(
        topBar = {
            CompactTopAppBar(
                title = { Text("Weekly Report") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
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
            // Week Navigation Header
            item {
                WeekNavigationHeader(
                    weekOffset = currentWeekOffset,
                    onPreviousWeek = { currentWeekOffset-- },
                    onNextWeek = { currentWeekOffset++ }
                )
            }
            
            // Calendar Day Selector
            item {
                CalendarDaySelector(
                    weekDates = weekDates,
                    selectedDayIndex = selectedDayIndex,
                    onDaySelected = { selectedDayIndex = it },
                    weekActivities = weekActivities
                )
            }
            
            // Summary Cards - 2x2 Grid Layout
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    // First Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Duration",
                            value = "$totalDuration min",
                            color = InfoLight
                        )
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Burned",
                            value = "$totalCalories kcal",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    // Second Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Distance",
                            value = String.format(java.util.Locale.US, "%.1f km", totalDistance),
                            color = SuccessLight
                        )
                        SummaryCard(
                            modifier = Modifier.weight(1f),
                            title = "Intake",
                            value = "$calorieIntake kcal",
                            color = ActivityJogging
                        )
                    }
                }
            }
            
            // Activity Graph
            item {
                AppCard(elevation = 1) {
                    SectionHeader(title = "Activities")
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    
                    if (dailyActivities.isEmpty()) {
                        EmptyState(
                            title = "No Activities",
                            description = "No activities recorded for this day",
                            icon = Icons.Default.FitnessCenter
                        )
                    } else {
                        ActivityBarGraph(activities = dailyActivities)
                    }
                }
            }
            
            // Longest Activity
            if (longestActivity != null) {
                item {
                    AppCard(elevation = 1) {
                        SectionHeader(title = "Longest Duration")
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = longestActivity.activityType,
                                style = AppTypography.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${longestActivity.duration} min",
                                style = AppTypography.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeekNavigationHeader(
    weekOffset: Int,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    val weekDates = getWeekDates(weekOffset)
    val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    val startDate = dateFormat.format(Date(weekDates.first()))
    val endDate = dateFormat.format(Date(weekDates.last()))
    
    // Check if next week would contain future dates
    val nextWeekDates = getWeekDates(weekOffset + 1)
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis
    val canGoNext = nextWeekDates.first() <= today
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousWeek) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Previous Week")
        }
        
        Text(
            text = "$startDate - $endDate",
            style = AppTypography.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        IconButton(
            onClick = onNextWeek,
            enabled = canGoNext
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Next Week")
        }
    }
}

@Composable
fun CalendarDaySelector(
    weekDates: List<Long>,
    selectedDayIndex: Int,
    onDaySelected: (Int) -> Unit,
    weekActivities: List<ActivityLog>
) {
    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val dateFormat = SimpleDateFormat("d", Locale.getDefault())
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekDates.forEachIndexed { index, date ->
            val hasActivity = weekActivities.any { activity ->
                val activityCal = Calendar.getInstance().apply { timeInMillis = activity.date }
                val dateCal = Calendar.getInstance().apply { timeInMillis = date }
                activityCal.get(Calendar.DAY_OF_YEAR) == dateCal.get(Calendar.DAY_OF_YEAR) &&
                        activityCal.get(Calendar.YEAR) == dateCal.get(Calendar.YEAR)
            }
            
            DayButton(
                dayName = dayNames[index],
                dayNumber = dateFormat.format(Date(date)),
                isSelected = index == selectedDayIndex,
                hasActivity = hasActivity,
                onClick = { onDaySelected(index) }
            )
        }
    }
}

@Composable
fun DayButton(
    dayName: String,
    dayNumber: String,
    isSelected: Boolean,
    hasActivity: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.sm, horizontal = Spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dayName,
            style = AppTypography.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = dayNumber,
            style = AppTypography.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        )
        
        if (hasActivity) {
            Spacer(modifier = Modifier.height(Spacing.xs))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.primary
                    )
            )
        }
    }
}

@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier.height(90.dp), // Fixed height
        shape = Shapes.md,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(Spacing.md)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = AppTypography.typography.titleMedium, // Smaller default size
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = AppTypography.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ActivityBarGraph(activities: List<ActivityLog>) {
    val maxDuration = activities.maxOfOrNull { it.duration } ?: 1
    val activityColors = mapOf(
        "Running" to ActivityRunning,
        "Jogging" to ActivityJogging,
        "Cycling" to ActivityCycling,
        "Hiking" to ActivityHiking
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = Spacing.sm)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.Bottom
        ) {
            activities.forEach { activity ->
                val heightFraction = activity.duration.toFloat() / maxDuration.toFloat()
                val animatedHeight = remember { Animatable(0f) }
                
                LaunchedEffect(heightFraction) {
                    animatedHeight.animateTo(
                        targetValue = heightFraction,
                        animationSpec = tween(durationMillis = 800)
                    )
                }
                
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(animatedHeight.value)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(activityColors[activity.activityType] ?: Color.Gray)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.sm))
        
        // Activity labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            activities.forEach { activity ->
                Text(
                    text = activity.activityType.take(3),
                    style = AppTypography.typography.labelSmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                )
            }
        }
    }
}

// Helper functions
private fun getWeekDates(weekOffset: Int): List<Long> {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.WEEK_OF_YEAR, weekOffset)
    
    // Get Monday of the week
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val daysFromMonday = when (dayOfWeek) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        Calendar.SUNDAY -> 6
        else -> 0
    }
    
    calendar.add(Calendar.DAY_OF_YEAR, -daysFromMonday)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    
    return (0..6).map { dayOffset ->
        val date = calendar.clone() as Calendar
        date.add(Calendar.DAY_OF_YEAR, dayOffset)
        date.timeInMillis
    }
}

private fun getCurrentDayIndex(): Int {
    val today = Calendar.getInstance()
    return when (today.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        Calendar.SUNDAY -> 6
        else -> 0
    }
}
