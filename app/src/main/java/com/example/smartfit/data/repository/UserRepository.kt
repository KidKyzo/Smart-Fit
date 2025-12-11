package com.example.smartfit.data.repository

import android.content.Context
import com.example.smartfit.data.database.User
import com.example.smartfit.data.database.AppDatabase
import com.example.smartfit.data.datastore.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(context: Context) {
    
    private val userPreferences = UserPreferences(context)
    private val userDao = AppDatabase.getDatabase(context).userDao()

    val theme: Flow<Boolean> = userPreferences.themeFlow.map { it ?: false }
    val stepGoal: Flow<Int> = userPreferences.stepGoalFlow

    val isLoggedIn: Flow<Boolean> = userPreferences.isLoggedInFlow

    suspend fun login() {
        userPreferences.login()
    }

    suspend fun logout() {
        userPreferences.logout()
    }
    
    val stepTrackingData: Flow<Triple<Int, Int, Long>> = userPreferences.stepTrackingFlow
    
    suspend fun saveStepTrackingData(lastStepCount: Int, savedStepsToday: Int, lastTrackingDate: Long) {
        userPreferences.saveStepTrackingData(lastStepCount, savedStepsToday, lastTrackingDate)
    }
    
    // User Profile Methods
    val userProfile: Flow<User?> = userDao.getUserProfile()
    
    /**
     * Save or update user profile
     * Uses REPLACE strategy - old data is replaced, not duplicated
     */
    suspend fun saveUserProfile(name: String, age: Int, weight: Float, height: Float, gender: String) {
        val now = System.currentTimeMillis()
        val existingUser = userDao.getUser()
        
        val user = User(
            id = 1, // Always 1 for single-user app
            name = name,
            age = age,
            weight = weight,
            height = height,
            gender = gender,
            createdAt = existingUser?.createdAt ?: now,
            updatedAt = now
        )
        
        userDao.insertOrUpdateUser(user) // REPLACE strategy prevents duplication
    }
    
    suspend fun getUser(): User? = userDao.getUser()
}