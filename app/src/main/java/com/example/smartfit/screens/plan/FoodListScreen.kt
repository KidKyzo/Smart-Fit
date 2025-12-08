package com.example.smartfit.screens.plan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.smartfit.data.model.FoodData
import com.example.smartfit.data.model.MockFoodData
import com.example.smartfit.ui.designsystem.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodListScreen(
    navController: NavController
) {
    val foods = remember { MockFoodData.getFoods() }
    
    AppScaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food & Nutrition") },
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
            items(foods) { food ->
                FoodListItem(
                    food = food,
                    onClick = {
                        navController.navigate("food_detail/${food.id}")
                    }
                )
            }
        }
    }
}

@Composable
private fun FoodListItem(
    food: FoodData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            // Image placeholder
            Box(
                modifier = Modifier
                    .size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = food.title,
                    modifier = Modifier.size(Sizing.iconXLarge),
                    tint = MaterialTheme.colorScheme.secondary
                )
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
        }
    }
}
