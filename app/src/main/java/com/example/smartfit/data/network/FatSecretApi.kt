package com.example.smartfit.data.network

import com.example.smartfit.data.network.dto.FoodSearchResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * FatSecret Platform API Interface
 * All requests require Bearer token authentication
 */
interface FatSecretApi {
    
    /**
     * Search for foods by name
     * @param query Search term (e.g., "chicken breast")
     * @param maxResults Maximum number of results (default 20)
     */
    @POST("rest/server.api")
    @FormUrlEncoded
    suspend fun searchFoods(
        @Field("method") method: String = "foods.search",
        @Field("search_expression") query: String,
        @Field("format") format: String = "json",
        @Field("max_results") maxResults: Int = 50,
        @Field("page_number") pageNumber: Int = 0
    ): FoodSearchResponse
    
    /**
     * Get detailed food information by ID
     * @param foodId FatSecret food ID
     */
    @POST("rest/server.api")
    @FormUrlEncoded
    suspend fun getFoodDetails(
        @Field("method") method: String = "food.get.v4",
        @Field("food_id") foodId: String,
        @Field("format") format: String = "json"
    ): FoodSearchResponse
}
