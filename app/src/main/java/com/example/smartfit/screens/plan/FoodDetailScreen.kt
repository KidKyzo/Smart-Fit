package com.example.smartfit.screens.plan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartfit.data.model.MockFoodData
import com.example.smartfit.ui.designsystem.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailScreen(
    navController: NavController,
    foodId: Int
) {
    val food = remember { MockFoodData.getFoodById(foodId) }
    
    if (food == null) {
        // Handle food not found
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Food not found")
        }
        return
    }
    
    AppScaffold(
        topBar = {
            TopAppBar(
                title = { Text(food.title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
            // Image section
            item {
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    elevation = 2
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = food.title,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
            
            // Food name and serving size
            item {
                Column {
                    Text(
                        text = food.title,
                        style = AppTypography.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = "Serving Size: ${food.servingSize}",
                        style = AppTypography.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
                    )
                }
            }
            
            // Nutritional information
            item {
                AppCard(elevation = 1) {
                    Column {
                        Text(
                            text = "Nutritional Information",
                            style = AppTypography.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(Spacing.md))
                        
                        NutritionRow("Calories", "${food.calories} kcal")
                        HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                        NutritionRow("Protein", food.protein)
                        HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                        NutritionRow("Carbohydrates", food.carbs)
                        HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                        NutritionRow("Fats", food.fats)
                    }
                }
            }
            
            // Description
            item {
                AppCard(elevation = 1) {
                    Column {
                        Text(
                            text = "Description",
                            style = AppTypography.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = food.description,
                            style = AppTypography.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.high)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NutritionRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = AppTypography.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium)
        )
        Text(
            text = value,
            style = AppTypography.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
