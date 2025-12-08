package com.example.smartfit.data.repository

import android.content.Context
import com.example.smartfit.data.datastore.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(context: Context) {
    
    private val userPreferences = UserPreferences(context)

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
}