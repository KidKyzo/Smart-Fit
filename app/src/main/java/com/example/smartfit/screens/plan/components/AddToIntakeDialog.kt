package com.example.smartfit.screens.plan.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.smartfit.data.model.FoodData
import com.example.smartfit.ui.designsystem.Spacing

/**
 * Dialog for adding food to daily intake
 * Allows user to specify servings and meal type
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToIntakeDialog(
    food: FoodData,
    onDismiss: () -> Unit,
    onConfirm: (servings: Float, mealType: String) -> Unit
) {
    var servings by remember { mutableStateOf("1.0") }
    var selectedMeal by remember { mutableStateOf("Snack") }
    var expanded by remember { mutableStateOf(false) }
    
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")
    
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
                    style = MaterialTheme.typography.titleMedium
                )
                
                // Servings input
                OutlinedTextField(
                    value = servings,
                    onValueChange = { servings = it },
                    label = { Text("Servings") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Meal type dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedMeal,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Meal Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        mealTypes.forEach { meal ->
                            DropdownMenuItem(
                                text = { Text(meal) },
                                onClick = {
                                    selectedMeal = meal
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                // Calorie calculation
                val servingsFloat = servings.toFloatOrNull() ?: 1f
                val totalCalories = (servingsFloat * food.calories).toInt()
                
                Divider(modifier = Modifier.padding(vertical = Spacing.xs))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Calories:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "$totalCalories kcal",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val servingsFloat = servings.toFloatOrNull() ?: 1f
                    onConfirm(servingsFloat, selectedMeal)
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
