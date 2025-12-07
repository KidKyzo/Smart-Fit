package com.example.smartfit.data.repository

import android.content.Context
import com.example.smartfit.data.datastore.UserPreferences
import kotlinx.coroutines.flow.Flow

class UserRepository(context: Context) {
    
    private val userPreferences = UserPreferences(context)
    
    val theme: Flow<Boolean> = userPreferences.themeFlow
    
    suspend fun saveTheme(isDarkMode: Boolean) {
        userPreferences.saveTheme(isDarkMode)
    }
    
    val stepGoal: Flow<Int> = userPreferences.stepGoalFlow
    
    suspend fun saveStepGoal(goal: Int) {
        userPreferences.saveStepGoal(goal)
    }

    val isLoggedIn: Flow<Boolean> = userPreferences.isLoggedInFlow

    suspend fun login() {
        userPreferences.login()
    }

    suspend fun logout() {
        userPreferences.logout()
    }
}