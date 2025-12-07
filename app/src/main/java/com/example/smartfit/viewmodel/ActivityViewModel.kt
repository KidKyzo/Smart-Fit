package com.example.smartfit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartfit.data.database.ActivityLog
import com.example.smartfit.data.repository.ActivityRepository
import com.example.smartfit.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar

class ActivityViewModel(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _activities = MutableStateFlow<List<ActivityLog>>(emptyList())
    val activities: StateFlow<List<ActivityLog>> = _activities.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // StateFlows for new UI data
    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    private val _stepGoal = MutableStateFlow(10000)
    val stepGoal: StateFlow<Int> = _stepGoal.asStateFlow()
    
    private val _calories = MutableStateFlow(0)
    val calories: StateFlow<Int> = _calories.asStateFlow()

    private val _distance = MutableStateFlow(0.0)
    val distance: StateFlow<Double> = _distance.asStateFlow()

    private val _activeTime = MutableStateFlow(0)
    val activeTime: StateFlow<Int> = _activeTime.asStateFlow()

    private val _weeklyAvgSteps = MutableStateFlow(0)
    val weeklyAvgSteps: StateFlow<Int> = _weeklyAvgSteps.asStateFlow()

    private val _selectedActivity = MutableStateFlow<ActivityLog?>(null)
    val selectedActivity: StateFlow<ActivityLog?> = _selectedActivity.asStateFlow()

    private val _lastAddedActivity = MutableStateFlow<ActivityLog?>(null)
    val lastAddedActivity: StateFlow<ActivityLog?> = _lastAddedActivity.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Combine flows for activities and step goal
            combine(
                activityRepository.getAllActivities(),
                userRepository.stepGoal
            ) { activities, goal ->
                Pair(activities, goal)
            }.collect { (activityList, goal) ->
                _activities.value = activityList
                _stepGoal.value = goal
                updateStats(activityList)
                _isLoading.value = false
            }
        }
    }

    private fun updateStats(activities: List<ActivityLog>) {
        // Calculate stats for today
        val today = Calendar.getInstance()
        val todaysActivities = activities.filter {
            val activityDate = Calendar.getInstance().apply { timeInMillis = it.date }
            today.get(Calendar.YEAR) == activityDate.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == activityDate.get(Calendar.DAY_OF_YEAR)
        }

        _steps.value = todaysActivities.sumOf { it.steps ?: 0 }
        _calories.value = todaysActivities.sumOf { it.calories }
        _distance.value = todaysActivities.sumOf { it.distance }
        _activeTime.value = todaysActivities.sumOf { it.duration }

        // Calculate weekly average
        val sevenDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }.timeInMillis
        val recentActivities = activities.filter { it.date >= sevenDaysAgo }
        val totalStepsInWeek = recentActivities.sumOf { it.steps ?: 0 }
        _weeklyAvgSteps.value = if (recentActivities.isNotEmpty()) totalStepsInWeek / 7 else 0
    }

    fun addActivity(activity: ActivityLog) {
        viewModelScope.launch {
            activityRepository.insertActivity(activity)
            _lastAddedActivity.value = activity
        }
    }

    fun updateActivity(activity: ActivityLog) {
        viewModelScope.launch {
            activityRepository.updateActivity(activity)
        }
    }

    fun deleteActivity(activity: ActivityLog) {
        viewModelScope.launch {
            activityRepository.deleteActivity(activity)
        }
    }
    
    fun selectActivity(activity: ActivityLog) {
        _selectedActivity.value = activity
    }
    
    fun clearSelectedActivity() {
        _selectedActivity.value = null
    }

    fun clearLastAddedActivity() {
        _lastAddedActivity.value = null
    }
}

class ActivityViewModelFactory(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivityViewModel(activityRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}