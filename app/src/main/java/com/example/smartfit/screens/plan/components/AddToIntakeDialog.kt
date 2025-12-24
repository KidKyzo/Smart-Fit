package com.example.smartfit.screens.plan.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartfit.data.model.FoodData
import com.example.smartfit.data.model.ServingOption
import com.example.smartfit.data.model.calculateNutrition
import com.example.smartfit.ui.designsystem.Spacing

/**
 * Dialog for adding food to daily intake
 * Allows user to select serving size and meal type
 * Nutrition values update based on selected serving
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToIntakeDialog(
    food: FoodData,
    onDismiss: () -> Unit,
    onConfirm: (servings: Float, mealType: String) -> Unit
) {
    var selectedServingIndex by remember { mutableStateOf(0) }
    var selectedMeal by remember { mutableStateOf("Snack") }
    var servingExpanded by remember { mutableStateOf(false) }
    var mealExpanded by remember { mutableStateOf(false) }
    
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")
    val servingOptions = food.servingOptions
    val selectedServing = servingOptions[selectedServingIndex]
    
    // Calculate nutrition for selected serving
    val nutrition = food.calculateNutrition(selectedServing.grams)
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Intake") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // Food name
                Text(
                    text = food.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Serving size dropdown
                ExposedDropdownMenuBox(
                    expanded = servingExpanded,
                    onExpandedChange = { servingExpanded = !servingExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedServing.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Serving Size") },
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
                
                // Meal type dropdown
                ExposedDropdownMenuBox(
                    expanded = mealExpanded,
                    onExpandedChange = { mealExpanded = !mealExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedMeal,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Meal Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = mealExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = mealExpanded,
                        onDismissRequest = { mealExpanded = false }
                    ) {
                        mealTypes.forEach { meal ->
                            DropdownMenuItem(
                                text = { Text(meal) },
                                onClick = {
                                    selectedMeal = meal
                                    mealExpanded = false
                                }
                            )
                        }
                    }
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.xs))
                
                // Nutrition information for selected serving
                Text(
                    text = "Nutrition Information",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Calories:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${nutrition.calories} kcal",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Protein:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${"%.1f".format(nutrition.protein)}g",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Carbs:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${"%.1f".format(nutrition.carbs)}g",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Fats:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${"%.1f".format(nutrition.fats)}g",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Convert grams to servings (based on 100g)
                    val servings = selectedServing.grams / 100f
                    onConfirm(servings, selectedMeal)
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
