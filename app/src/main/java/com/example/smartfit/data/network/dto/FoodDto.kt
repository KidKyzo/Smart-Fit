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
 * Filters for Malaysian and Indonesian foods only
 */
fun FoodDto.toFoodData(): FoodData? {
    // Filter: Only include Malaysian and Indonesian foods
    if (!isFromMalaysiaOrIndonesia()) {
        return null
    }
    
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
    
    // Generate nutrition claim (allergen/dietary info) in formatted style
    val nutritionClaim = generateFormattedNutritionClaim(foodName, brandName)
    
    return FoodData(
        id = foodId.hashCode(),
        title = foodName,
        description = nutritionClaim,
        imageUrl = "",  // Empty - will use placeholder icon
        calories = calories,
        protein = "${String.format("%.1f", protein)}g",
        carbs = "${String.format("%.1f", carbs)}g",
        fats = "${String.format("%.1f", fats)}g",
        servingSize = "${servingSizeGrams.toInt()}g"
    )
}

/**
 * Check if food is from Malaysia or Indonesia
 */
private fun FoodDto.isFromMalaysiaOrIndonesia(): Boolean {
    val malaysianIndonesianKeywords = listOf(
        // Indonesian foods
        "nasi goreng", "rendang", "satay", "sate", "gado-gado", "soto", "bakso",
        "mie goreng", "nasi uduk", "ayam goreng", "sambal", "tempeh", "tahu",
        "gudeg", "rawon", "sop buntut", "martabak", "pisang goreng", "lemper",
        "klepon", "onde-onde", "kue", "serabi", "es campur", "cendol",
        
        // Malaysian foods
        "nasi lemak", "roti canai", "laksa", "char kway teow", "mee goreng",
        "rendang", "satay", "nasi kerabu", "nasi dagang", "murtabak",
        "roti jala", "kuih", "onde onde", "apam", "curry", "sambal",
        "ikan bakar", "ayam percik", "nasi kandar", "rojak", "cendol",
        "teh tarik", "milo", "kaya", "pandan", "coconut"
    )
    
    val foodNameLower = foodName.lowercase()
    val brandNameLower = brandName?.lowercase() ?: ""
    
    return malaysianIndonesianKeywords.any { keyword ->
        foodNameLower.contains(keyword) || brandNameLower.contains(keyword)
    }
}

/**
 * Generate formatted nutrition claim with checkboxes
 * Format matches the image: "This food is free from: ✓ Milk ✓ Lactose..."
 */
private fun generateFormattedNutritionClaim(foodName: String, brandName: String?): String {
    val foodLower = foodName.lowercase()
    val brandLower = brandName?.lowercase() ?: ""
    val combined = "$foodLower $brandLower"
    
    val freeFrom = mutableListOf<String>()
    val contains = mutableListOf<String>()
    val notSuitableFor = mutableListOf<String>()
    
    // Check common allergens
    val allergens = mapOf(
        "Milk" to listOf("milk", "dairy", "cheese", "susu"),
        "Lactose" to listOf("lactose"),
        "Gluten" to listOf("wheat", "flour", "mie", "roti", "bread", "tepung"),
        "Soy" to listOf("soy", "kedelai", "tofu", "tahu"),
        "Sesame" to listOf("sesame", "wijen"),
        "Egg" to listOf("egg", "telur"),
        "Fish" to listOf("fish", "ikan"),
        "Nuts" to listOf("nut", "almond", "cashew", "kacang"),
        "Peanuts" to listOf("peanut", "kacang tanah"),
        "Shellfish" to listOf("shellfish", "shrimp", "udang", "crab", "kepiting")
    )
    
    allergens.forEach { (allergen, keywords) ->
        val hasAllergen = keywords.any { combined.contains(it) }
        if (hasAllergen) {
            contains.add(allergen)
        } else {
            freeFrom.add(allergen)
        }
    }
    
    // Check dietary suitability
    if (combined.contains("chicken") || combined.contains("ayam") || 
        combined.contains("beef") || combined.contains("daging") ||
        combined.contains("fish") || combined.contains("ikan") ||
        combined.contains("meat") || combined.contains("rendang") ||
        combined.contains("sate") || combined.contains("satay")) {
        notSuitableFor.add("Vegetarian")
        notSuitableFor.add("Vegan")
    }
    
    // Build formatted claim string
    val parts = mutableListOf<String>()
    
    if (freeFrom.isNotEmpty()) {
        val items = freeFrom.take(5).joinToString(" ") { "✓ $it" }
        parts.add("This food is free from:\n$items")
    }
    
    if (contains.isNotEmpty()) {
        val items = contains.joinToString(" ") { "✗ $it" }
        parts.add("This food contains:\n$items")
    }
    
    if (notSuitableFor.isNotEmpty()) {
        val items = notSuitableFor.joinToString(" ") { "✗ $it" }
        parts.add("This food is not suitable for $items diets")
    }
    
    return parts.joinToString("\n\n").ifEmpty { "No allergen information available" }
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

private fun extractServingSize(text: String): String {
    val grams = extractServingSizeGrams(text)
    return "${grams.toInt()}g"
}
