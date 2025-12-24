package com.example.smartfit.screens.plan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smartfit.data.model.FoodData
import com.example.smartfit.data.model.calculateNutrition
import com.example.smartfit.screens.plan.components.AddToIntakeDialog
import com.example.smartfit.ui.designsystem.*
import com.example.smartfit.viewmodel.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailScreen(
    navController: NavController,
    foodId: Int,
    foodViewModel: FoodViewModel
) {
    val foods by foodViewModel.searchResults.collectAsState()
    val food = remember(foods, foodId) {
        foods.firstOrNull { it.id == foodId }
    }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedServingIndex by remember { mutableStateOf(0) }
    var servingExpanded by remember { mutableStateOf(false) }

    if (food == null) {
        // Handle food not found
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Food not found")
                Spacer(modifier = Modifier.height(Spacing.md))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Go Back")
                }
            }
        }
        return
    }
    
    val servingOptions = food.servingOptions
    
    // Recalculate nutrition when serving size changes
    val selectedServing = servingOptions.getOrElse(selectedServingIndex) { servingOptions.first() }
    val nutrition = remember(selectedServingIndex, food.id) {
        val serving = servingOptions.getOrElse(selectedServingIndex) { servingOptions.first() }
        food.calculateNutrition(serving.grams)
    }
    
    AppScaffold(
        topBar = {
            CompactTopAppBar(
                title = { Text(food.title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add to intake",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Food Image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (food.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = food.imageUrl,
                            contentDescription = food.title,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = food.title,
                            modifier = Modifier.size(Sizing.iconXXLarge),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
            
            // Food Name
            item {
                Text(
                    text = food.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Serving Size Selector
            item {
                AppCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md)
                    ) {
                        Text(
                            text = "Serving Size",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        
                        ExposedDropdownMenuBox(
                            expanded = servingExpanded,
                            onExpandedChange = { servingExpanded = !servingExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedServing.label,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = servingExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                            )
                            
                            ExposedDropdownMenu(
                                expanded = servingExpanded,
                                onDismissRequest = { servingExpanded = false }
                            ) {
                                servingOptions.forEachIndexed { index, serving ->
                                    DropdownMenuItem(
                                        text = { Text(serving.label) },
                                        onClick = {
                                            selectedServingIndex = index
                                            servingExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Nutritional Information
            item {
                AppCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Text(
                            text = "Nutritional Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.xs))
                        
                        NutritionRow("Calories", "${nutrition.calories} kcal")
                        NutritionRow("Protein", "${String.format("%.1f", nutrition.protein)}g")
                        NutritionRow("Carbohydrates", "${String.format("%.1f", nutrition.carbs)}g")
                        NutritionRow("Fats", "${String.format("%.1f", nutrition.fats)}g")
                    }
                }
            }
        }
    }
    
    // Add to Intake Dialog
    if (showAddDialog) {
        AddToIntakeDialog(
            food = food,
            onDismiss = { showAddDialog = false },
            onConfirm = { servings, mealType ->
                foodViewModel.logFood(food, servings, mealType)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun NutritionRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
