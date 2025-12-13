package com.example.smartfit.screens.plan.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.smartfit.data.model.FoodData
import com.example.smartfit.ui.designsystem.*
import com.example.smartfit.viewmodel.FoodViewModel

@Composable
fun CalorieIntakeSection(
    foodViewModel: FoodViewModel,
    onFoodClick: (FoodData) -> Unit,
    onViewMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val foods by foodViewModel.searchResults.collectAsState()
    
    // Load initial foods if empty
    LaunchedEffect(Unit) {
        if (foods.isEmpty()) {
            foodViewModel.searchFoods("healthy")
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm)
    ) {
        // Header with View More button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Calorie Intake",
                style = AppTypography.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "View More",
                style = AppTypography.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onViewMoreClick() }
            )
        }
        
        Spacer(modifier = Modifier.height(Spacing.md))
        
        // Horizontal scrolling food cards (matching WorkoutSection style)
        LazyRow(
            contentPadding = PaddingValues(horizontal = Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            items(foods.take(5)) { food ->
                FoodCard(
                    food = food,
                    onAddToIntake = { servings, mealType ->
                        foodViewModel.logFood(food, servings, mealType)
                    },
                    onClick = { onFoodClick(food) }
                )
            }
        }
    }
}

@Composable
private fun FoodCard(
    food: FoodData,
    onAddToIntake: (servings: Float, mealType: String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    
    AppCard(
        modifier = modifier
            .width(160.dp)
            .height(180.dp)
            .clickable { onClick() },
        elevation = 2
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Food Image (matching WorkoutSection layout)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                if (food.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = food.imageUrl,
                        contentDescription = food.title,
                        modifier = Modifier.size(Sizing.iconXLarge)
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
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.sm)
            ) {
                Text(
                    text = food.title,
                    style = AppTypography.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${food.calories} kcal",
                        style = AppTypography.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.medium),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    // Add button
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add to intake",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
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
                onAddToIntake(servings, mealType)
                showAddDialog = false
            }
        )
    }
}
