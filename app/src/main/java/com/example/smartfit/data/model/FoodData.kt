package com.example.smartfit.data.model

/**
 * Food data model with serving size options
 */
data class FoodData(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    val calories: Int,  // Per 100g base
    val protein: String,  // Per 100g base
    val carbs: String,  // Per 100g base
    val fats: String,  // Per 100g base
    val servingSize: String,
    val servingOptions: List<ServingOption> = getDefaultServingOptions()
)

/**
 * Serving size option with weight
 */
data class ServingOption(
    val label: String,
    val grams: Float
)

/**
 * Get default serving options
 */
fun getDefaultServingOptions(): List<ServingOption> {
    return listOf(
        ServingOption("100 g", 100f),
        ServingOption("1 serving (240 g)", 240f),
        ServingOption("1 cup (200 g)", 200f),
        ServingOption("1 packet (150 g)", 150f)
    )
}

/**
 * Calculate nutrition for specific serving size
 */
fun FoodData.calculateNutrition(servingGrams: Float): NutritionValues {
    val multiplier = servingGrams / 100f
    
    return NutritionValues(
        calories = (calories * multiplier).toInt(),
        protein = (protein.replace("g", "").toFloatOrNull()?.times(multiplier) ?: 0f),
        carbs = (carbs.replace("g", "").toFloatOrNull()?.times(multiplier) ?: 0f),
        fats = (fats.replace("g", "").toFloatOrNull()?.times(multiplier) ?: 0f)
    )
}

/**
 * Nutrition values for a specific serving
 */
data class NutritionValues(
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float
)
