package com.example.smartfit.screens.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.smartfit.data.database.FoodIntakeLog
import com.example.smartfit.ui.designsystem.*
import com.example.smartfit.viewmodel.FoodViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieIntakeHistoryScreen(
    navController: NavController,
    foodViewModel: FoodViewModel
) {
    val foodIntake by foodViewModel.todayFoodIntake.collectAsState()
    var sortNewest by remember { mutableStateOf(true) }
    var showSortMenu by remember { mutableStateOf(false) }
    
    // Snackbar state for undo
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var deletedItem by remember { mutableStateOf<FoodIntakeLog?>(null) }
    var restoredItemId by remember { mutableStateOf<Long?>(null) }
    
    // Show snackbar when an item is deleted - this persists across recompositions
    LaunchedEffect(deletedItem) {
        deletedItem?.let { item ->
            val result = snackbarHostState.showSnackbar(
                message = "${item.foodName} removed",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            
            if (result == SnackbarResult.ActionPerformed) {
                // Undo deletion and track the restored item's ID
                foodViewModel.undoDeleteFoodIntake(item)
                restoredItemId = item.id
            }
            
            // Clear deleted item after handling
            deletedItem = null
        }
    }
    
    // Group foods by day and sort
    val groupedFoods = remember(foodIntake, sortNewest) {
        val sorted = if (sortNewest) {
            foodIntake.sortedByDescending { it.date }
        } else {
            foodIntake.sortedBy { it.date }
        }
        
        sorted.groupBy { log ->
            // Group by day (remove time component)
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
                title = { Text("Calorie Intake History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Sort button
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }
                    
                    // Sort dropdown menu
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Newest First") },
                            onClick = {
                                sortNewest = true
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Oldest First") },
                            onClick = {
                                sortNewest = false
                                showSortMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (groupedFoods.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Text(
                            text = "No food intake logged",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                        )
                        Text(
                            text = "Start logging your meals to see them here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    // Sort indicator
                    item {
                        Text(
                            text = "Sorted by: ${if (sortNewest) "Newest First" else "Oldest First"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                        )
                    }
                    
                    // Group by day
                    groupedFoods.forEach { (dayTimestamp, foodsForDay) ->
                        // Day header
                        item {
                            DayHeader(
                                dayTimestamp = dayTimestamp,
                                totalCalories = foodsForDay.sumOf { (it.calories * it.servings).toInt() }
                            )
                        }
                        
                        // Foods for this day with swipe-to-delete
                        items(
                            items = foodsForDay,
                            key = { it.id }
                        ) { food ->
                            SwipeToDeleteItem(
                                food = food,
                                restoredItemId = restoredItemId,
                                onRestoredItemReset = { restoredItemId = null },
                                onDelete = {
                                    deletedItem = food
                                    foodViewModel.deleteFoodIntake(food)
                                }
                            )
                        }
                        
                        // Spacer between days
                        item {
                            Spacer(modifier = Modifier.height(Spacing.sm))
                        }
                    }
                }
            }
            
            // Snackbar host at bottom - ALWAYS visible regardless of empty/non-empty state
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}


@Composable
private fun DayHeader(
    dayTimestamp: Long,
    totalCalories: Int
) {
    val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
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
    
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 1
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = displayDate,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (displayDate != dateString) {
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(dayTimestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$totalCalories kcal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteItem(
    food: FoodIntakeLog,
    restoredItemId: Long?,
    onRestoredItemReset: () -> Unit,
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
    
    // Reset dismiss state when this item was restored via undo
    LaunchedEffect(restoredItemId) {
        if (restoredItemId == food.id) {
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            onRestoredItemReset()
        }
    }
    
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            // Calculate swipe progress (0.0 to 1.0)
            val progress = dismissState.progress
            
            // Red background with opacity based on swipe progress
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(Shapes.md)
                    .background(MaterialTheme.colorScheme.error.copy(alpha = progress * 0.3f)),
                contentAlignment = Alignment.CenterEnd
            ) {
                // Delete icon that fades in as you swipe
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
        FoodIntakeItem(food = food)
    }
}


@Composable
private fun FoodIntakeItem(
    food: FoodIntakeLog
) {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val timeString = timeFormat.format(Date(food.date))
    
    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = food.foodName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Text(
                        text = timeString,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                    Text(
                        text = food.mealType,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                Text(
                    text = "${food.servingSize} × ${food.servings}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
            
            // Calorie count on the right
            Text(
                text = "${(food.calories * food.servings).toInt()} kcal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
