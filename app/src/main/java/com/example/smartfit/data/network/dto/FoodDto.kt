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
 * Parses food_description: "Per 100g - Calories: 165kcal | Fat: 3.60g | Carbs: 0.00g | Protein: 31.00g"
 */
fun FoodDto.toFoodData(): FoodData {
    val description = foodDescription
    
    // Extract nutritional values from description
    val calories = extractValue(description, "Calories:", "kcal")
    val protein = extractValue(description, "Protein:", "g")
    val carbs = extractValue(description, "Carbs:", "g")
    val fats = extractValue(description, "Fat:", "g")
    
    // Extract serving size
    val servingSize = extractServingSize(description)
    
    return FoodData(
        id = foodId.hashCode(),  // Convert string ID to int
        title = foodName,
        description = brandName?.let { "$foodName ($it)" } ?: foodName,
        imageUrl = "",  // FatSecret basic tier doesn't provide images
        calories = calories.toIntOrNull() ?: 0,
        protein = "${protein}g",
        carbs = "${carbs}g",
        fats = "${fats}g",
        servingSize = servingSize
    )
}

private fun extractValue(text: String, label: String, unit: String): String {
    val regex = "$label\\s*([\\d.]+)$unit".toRegex()
    return regex.find(text)?.groupValues?.get(1) ?: "0"
}

private fun extractServingSize(text: String): String {
    // Extract "Per 100g" or similar
    val regex = "Per\\s+([^-]+)".toRegex()
    return regex.find(text)?.groupValues?.get(1)?.trim() ?: "100g"
}
