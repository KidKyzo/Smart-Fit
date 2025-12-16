package com.example.smartfit.screens.plan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smartfit.data.model.FoodData
import com.example.smartfit.screens.plan.components.AddToIntakeDialog
import com.example.smartfit.ui.designsystem.*
import com.example.smartfit.viewmodel.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodListScreen(
    navController: NavController,
    foodViewModel: FoodViewModel
) {
    val foods by foodViewModel.searchResults.collectAsState()
    val isLoading by foodViewModel.isSearching.collectAsState()
    val error by foodViewModel.searchError.collectAsState()
    val hasMore by foodViewModel.hasMore.collectAsState()
    val sortOption by foodViewModel.sortOption.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var showSortMenu by remember { mutableStateOf(false) }
    
    // Load initial Indonesian/Malaysian foods
    LaunchedEffect(Unit) {
        if (foods.isEmpty()) {
            foodViewModel.searchFoods("nasi goreng")
        }
    }
    
    AppScaffold(
        topBar = {
            CompactTopAppBar(
                title = { Text("Food & Nutrition") },
                navigationIcon = {
                    IconButton(onClick = { 
                        foodViewModel.resetSearchAndSort()
                        navController.popBackStack()
                    }) {
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
                            text = { Text("Lowest Calories") },
                            onClick = {
                                foodViewModel.setSortOption(FoodViewModel.SortOption.LOWEST_CALORIES)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Highest Calories") },
                            onClick = {
                                foodViewModel.setSortOption(FoodViewModel.SortOption.HIGHEST_CALORIES)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("A to Z") },
                            onClick = {
                                foodViewModel.setSortOption(FoodViewModel.SortOption.A_TO_Z)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Z to A") },
                            onClick = {
                                foodViewModel.setSortOption(FoodViewModel.SortOption.Z_TO_A)
                                showSortMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    if (it.length >= 3) {
                        foodViewModel.searchFoods(it)
                    } else if (it.isEmpty()) {
                        foodViewModel.searchFoods("nasi goreng")
                    }
                },
                label = { Text("Search foods...") },
                leadingIcon = { 
                    Icon(Icons.Default.Search, contentDescription = "Search") 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                singleLine = true
            )
            
            // Current sort indicator
            Text(
                text = "Sorted by: ${getSortLabel(sortOption)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.xs)
            )
            
            // Error message
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = Spacing.lg),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            // Loading indicator
            if (isLoading && foods.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Food List with infinite scroll
                val listState = rememberLazyListState()
                
                // Detect when user reaches bottom
                LaunchedEffect(listState) {
                    snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                        .collect { lastVisibleIndex ->
                            if (lastVisibleIndex != null && 
                                lastVisibleIndex >= foods.size - 3 && 
                                hasMore && 
                                !isLoading) {
                                // Load more when 3 items from bottom
                                foodViewModel.loadMore()
                            }
                        }
                }
                
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    items(foods) { food ->
                        FoodListItem(
                            food = food,
                            onAddToIntake = { servings, mealType ->
                                foodViewModel.logFood(food, servings, mealType)
                            },
                            onClick = {
                                navController.navigate("food_detail/${food.id}")
                            }
                        )
                    }
                    
                    // Loading indicator at bottom when fetching more
                    if (isLoading && foods.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.md),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    
                    if (foods.isEmpty() && !isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.xl),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No foods found. Try a different search.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun getSortLabel(sortOption: FoodViewModel.SortOption): String {
    return when (sortOption) {
        FoodViewModel.SortOption.LOWEST_CALORIES -> "Lowest Calories"
        FoodViewModel.SortOption.HIGHEST_CALORIES -> "Highest Calories"
        FoodViewModel.SortOption.A_TO_Z -> "A to Z"
        FoodViewModel.SortOption.Z_TO_A -> "Z to A"
    }
}

@Composable
private fun FoodListItem(
    food: FoodData,
    onAddToIntake: (servings: Float, mealType: String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = 1
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Image (Coil with fallback icon)
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                if (food.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = food.imageUrl,
                        contentDescription = food.title,
                        modifier = Modifier.size(80.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = food.title,
                        modifier = Modifier.size(Sizing.iconXLarge),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            
            // Title and description
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = food.title,
                    style = AppTypography.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = food.description,
                    style = AppTypography.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Text(
                        text = "${food.calories} kcal",
                        style = AppTypography.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "•",
                        style = AppTypography.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                    )
                    Text(
                        text = food.servingSize,
                        style = AppTypography.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                    )
                }
            }
            
            // Add to Intake Button
            IconButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add to intake",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
    
    // Add to Intake Dialog
    if (showAddDialog) {
        AddToIntakeDialog(
            food = food,
            onDismiss = { showAddDialog = false },
            onConfirm = { servings, mealType ->
                onAddToIntake(servings, mealType)
                showAddDialog = false
            }
        )
    }
}
