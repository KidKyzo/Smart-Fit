package com.example.smartfit.data.network.dto

import com.example.smartfit.data.model.FoodData
import com.google.gson.annotations.SerializedName

/**
 * FatSecret API Food Search Response
 */
data class FoodSearchResponse(
    @SerializedName("foods") val foods: FoodsContainer?
)

data class FoodsContainer(
    @SerializedName("food") val food: List<FoodDto>?,
    @SerializedName("max_results") val maxResults: String?,
    @SerializedName("total_results") val totalResults: String?
)

/**
 * FatSecret Food DTO
 */
data class FoodDto(
    @SerializedName("food_id") val foodId: String,
    @SerializedName("food_name") val foodName: String,
    @SerializedName("food_description") val foodDescription: String,
    @SerializedName("brand_name") val brandName: String?,
    @SerializedName("food_type") val foodType: String?,
    @SerializedName("food_url") val foodUrl: String?
)

/**
 * Extension function to convert FatSecret DTO to domain model
 * Accepts all foods from FatSecret API (no regional filtering)
 */
fun FoodDto.toFoodData(): FoodData {
    val description = foodDescription
    
    // Extract serving size first to normalize nutrition values
    val servingSizeGrams = extractServingSizeGrams(description)
    
    // Extract nutritional values from description
    val caloriesRaw = extractValue(description, "Calories:", "kcal")
    val proteinRaw = extractValue(description, "Protein:", "g")
    val carbsRaw = extractValue(description, "Carbs:", "g")
    val fatsRaw = extractValue(description, "Fat:", "g")
    
    // Normalize to per 100g (API values are for the serving size in description)
    val multiplier = 100f / servingSizeGrams
    val calories = (caloriesRaw.toFloatOrNull()?.times(multiplier) ?: 0f).toInt()
    val protein = (proteinRaw.toFloatOrNull()?.times(multiplier) ?: 0f)
    val carbs = (carbsRaw.toFloatOrNull()?.times(multiplier) ?: 0f)
    val fats = (fatsRaw.toFloatOrNull()?.times(multiplier) ?: 0f)
    
    return FoodData(
        id = foodId.hashCode(),
        title = foodName,
        description = "",  // No dietary/allergen descriptions
        imageUrl = "",  // Empty - will use placeholder icon
        calories = calories,
        protein = "${"%.1f".format(protein)}g",
        carbs = "${"%.1f".format(carbs)}g",
        fats = "${"%.1f".format(fats)}g",
        servingSize = "${servingSizeGrams.toInt()}g"
    )
}

private fun extractValue(text: String, label: String, unit: String): String {
    val regex = "$label\\s*([\\d.]+)$unit".toRegex()
    return regex.find(text)?.groupValues?.get(1) ?: "0"
}

private fun extractServingSizeGrams(text: String): Float {
    // Extract "Per 100g" or similar from description
    val perRegex = "Per\\s+(\\d+\\.?\\d*)\\s*g".toRegex(RegexOption.IGNORE_CASE)
    val match = perRegex.find(text)
    
    return if (match != null) {
        match.groupValues[1].toFloatOrNull() ?: 100f
    } else {
        100f  // Default to 100g
    }
}
