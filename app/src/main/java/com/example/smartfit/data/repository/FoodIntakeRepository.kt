package com.example.smartfit.data.repository

import com.example.smartfit.data.database.FoodIntakeDao
import com.example.smartfit.data.database.FoodIntakeLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.Calendar

/**
 * Repository for Food Intake tracking
 * Handles database operations for consumed foods with multi-user support
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class FoodIntakeRepository(
    private val foodIntakeDao: FoodIntakeDao,
    private val userRepository: UserRepository
) {
    
    /**
     * Get today's food intake logs for current user
     */
    fun getTodayFoodIntake(): Flow<List<FoodIntakeLog>> {
        return userRepository.currentUserId.flatMapLatest { userId ->
            if (userId != null) {
                val (startOfDay, endOfDay) = getTodayBounds()
                foodIntakeDao.getFoodIntakeForDay(userId, startOfDay, endOfDay)
            } else {
                flowOf(emptyList())
            }
        }
    }
    
    /**
     * Get total calories consumed today for current user
     */
    suspend fun getTodayTotalCalories(): Int {
        val userId = userRepository.currentUserId.first() ?: return 0
        val (startOfDay, endOfDay) = getTodayBounds()
        return foodIntakeDao.getTotalCaloriesForDay(userId, startOfDay, endOfDay) ?: 0
    }
    
    /**
     * Get total calories for a specific date
     */
    suspend fun getCaloriesForDate(date: Long): Int {
        val userId = userRepository.currentUserId.first() ?: return 0
        val (startOfDay, endOfDay) = getDayBounds(date)
        return foodIntakeDao.getTotalCaloriesForDay(userId, startOfDay, endOfDay) ?: 0
    }
    
    /**
     * Log food intake for current user
     * Automatically sets userId from currentUserId
     */
    suspend fun logFoodIntake(foodIntake: FoodIntakeLog) {
        val userId = userRepository.currentUserId.first() ?: return
        foodIntakeDao.insertFoodIntake(foodIntake.copy(userId = userId))
    }
    
    /**
     * Delete food intake log
     */
    suspend fun deleteFoodIntake(foodIntake: FoodIntakeLog) {
        foodIntakeDao.deleteFoodIntake(foodIntake)
    }
    
    /**
     * Reinsert food intake log (for undo)
     */
    suspend fun reinsertFoodIntake(foodIntake: FoodIntakeLog) {
        foodIntakeDao.insertFoodIntake(foodIntake)
    }
    
    /**
     * Get start and end of today in milliseconds
     */
    private fun getTodayBounds(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis
        
        return Pair(startOfDay, endOfDay)
    }
    
    /**
     * Get start and end of a specific day in milliseconds
     */
    private fun getDayBounds(date: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis
        
        return Pair(startOfDay, endOfDay)
    }
}
