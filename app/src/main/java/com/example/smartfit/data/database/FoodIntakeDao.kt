package com.example.smartfit.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Food Intake operations
 * All queries filter by userId for multi-user support
 */
@Dao
interface FoodIntakeDao {
    
    /**
     * Get all food intake logs for a specific day and user
     */
    @Query("""
        SELECT * FROM food_intake_logs 
        WHERE userId = :userId 
        AND date >= :startOfDay 
        AND date < :endOfDay 
        ORDER BY date DESC
    """)
    fun getFoodIntakeForDay(userId: Int, startOfDay: Long, endOfDay: Long): Flow<List<FoodIntakeLog>>
    
    /**
     * Get total calories consumed for a specific day
     */
    @Query("""
        SELECT SUM(calories * servings) 
        FROM food_intake_logs 
        WHERE userId = :userId 
        AND date >= :startOfDay 
        AND date < :endOfDay
    """)
    suspend fun getTotalCaloriesForDay(userId: Int, startOfDay: Long, endOfDay: Long): Int?
    
    /**
     * Get food intake logs for the last N days
     */
    @Query("""
        SELECT * FROM food_intake_logs 
        WHERE userId = :userId 
        AND date >= :startDate 
        ORDER BY date DESC
    """)
    fun getFoodIntakeForPeriod(userId: Int, startDate: Long): Flow<List<FoodIntakeLog>>
    
    /**
     * Insert food intake log
     */
    @Insert
    suspend fun insertFoodIntake(foodIntake: FoodIntakeLog)
    
    /**
     * Delete food intake log
     */
    @Delete
    suspend fun deleteFoodIntake(foodIntake: FoodIntakeLog)
    
    /**
     * Delete food intake by ID
     */
    @Query("DELETE FROM food_intake_logs WHERE id = :id AND userId = :userId")
    suspend fun deleteFoodIntakeById(id: Long, userId: Int)
}
