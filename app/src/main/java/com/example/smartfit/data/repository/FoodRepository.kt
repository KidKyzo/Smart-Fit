package com.example.smartfit.data.repository

import android.util.Log
import com.example.smartfit.data.model.FoodData
import com.example.smartfit.data.network.FatSecretApi
import com.example.smartfit.data.network.dto.toFoodData

/**
 * Repository for FatSecret API food data
 * Handles network requests for food search and details
 */
class FoodRepository(
    private val api: FatSecretApi
) {
    
    /**
     * Search for foods by name
     * @param query Search term (e.g., "chicken breast")
     * @return Result with list of foods or error
     */
    suspend fun searchFoods(query: String): Result<List<FoodData>> {
        return try {
            Log.d("FoodRepository", "Searching for: $query")
            val response = api.searchFoods(query = query)
            Log.d("FoodRepository", "API Response: $response")
            
            val foods = response.foods?.food?.map { 
                Log.d("FoodRepository", "Food DTO: $it")
                it.toFoodData() 
            } ?: emptyList()
            
            Log.d("FoodRepository", "Converted ${foods.size} foods")
            Result.success(foods)
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error searching foods", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get detailed food information
     * @param foodId FatSecret food ID
     * @return Result with food data or error
     */
    suspend fun getFoodDetails(foodId: String): Result<FoodData> {
        return try {
            val response = api.getFoodDetails(foodId = foodId)
            val food = response.foods?.food?.firstOrNull()?.toFoodData()
                ?: throw IllegalStateException("Food not found")
            Result.success(food)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
