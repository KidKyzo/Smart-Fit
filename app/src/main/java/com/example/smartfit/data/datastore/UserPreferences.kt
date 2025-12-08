
package com.example.smartfit.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to create the DataStore singleton
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferences(private val context: Context) {

    companion object {
        val THEME_KEY = booleanPreferencesKey("is_dark_mode")
        val STEP_GOAL_KEY = intPreferencesKey("step_goal")
        val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        
        // Keys for step tracking
        val LAST_STEP_COUNT_KEY = intPreferencesKey("last_step_count")
        val SAVED_STEPS_TODAY_KEY = intPreferencesKey("saved_steps_today")
        val LAST_TRACKING_DATE_KEY = longPreferencesKey("last_tracking_date")
    }

    // Get the theme setting (default to false if not set)
    val themeFlow: Flow<Boolean?>
        get() = context.dataStore.data
            .map { preferences ->
                preferences[THEME_KEY]
            }

    // Save the theme setting
    suspend fun saveTheme(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkMode
        }
    }

    // Get the step goal
    val stepGoalFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[STEP_GOAL_KEY] ?: 10000 // Default goal is 10,000
        }

    // Save the step goal
    suspend fun saveStepGoal(goal: Int) {
        context.dataStore.edit { preferences ->
            preferences[STEP_GOAL_KEY] = goal
        }
    }

    // Get the logged in status
    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN_KEY] ?: false
        }

    // Set logged in status to true
    suspend fun login() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }

    // Set logged in status to false
    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = false
        }
    }

    // Step Tracking Methods
    val stepTrackingFlow: Flow<Triple<Int, Int, Long>> = context.dataStore.data
        .map { preferences ->
            Triple(
                preferences[LAST_STEP_COUNT_KEY] ?: 0,
                preferences[SAVED_STEPS_TODAY_KEY] ?: 0,
                preferences[LAST_TRACKING_DATE_KEY] ?: 0L
            )
        }

    suspend fun saveStepTrackingData(lastStepCount: Int, savedStepsToday: Int, lastTrackingDate: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_STEP_COUNT_KEY] = lastStepCount
            preferences[SAVED_STEPS_TODAY_KEY] = savedStepsToday
            preferences[LAST_TRACKING_DATE_KEY] = lastTrackingDate
        }
    }
}