package com.example.smartfit.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartfit.data.database.ActivityLog
import com.example.smartfit.data.repository.ActivityRepository
import com.example.smartfit.data.repository.UserRepository
import com.example.smartfit.utils.StepSensor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar
import android.util.Log

class ActivityViewModel(
    private val application: Application,
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val TAG = "DebugSmartApp"

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
    
    private val _averageSpeed = MutableStateFlow(0.0)
    val averageSpeed: StateFlow<Double> = _averageSpeed.asStateFlow()

    private val _weeklyAvgSteps = MutableStateFlow(0)
    val weeklyAvgSteps: StateFlow<Int> = _weeklyAvgSteps.asStateFlow()

    private val _selectedActivity = MutableStateFlow<ActivityLog?>(null)
    val selectedActivity: StateFlow<ActivityLog?> = _selectedActivity.asStateFlow()

    private val _lastAddedActivity = MutableStateFlow<ActivityLog?>(null)
    val lastAddedActivity: StateFlow<ActivityLog?> = _lastAddedActivity.asStateFlow()
    
    private val _dailyStepsLast7Days = MutableStateFlow<List<Int>>(emptyList())
    val dailyStepsLast7Days: StateFlow<List<Int>> = _dailyStepsLast7Days.asStateFlow()

    // Step Tracking
    private var stepSensorJob: Job? = null
    private val stepSensor = StepSensor(application)

    // Constants for calculation
    private val STRIDE_LENGTH_METERS = 0.762 // Average stride length
    private val CALORIES_PER_STEP = 0.04 // Average calories per step
    private val STEPS_PER_MINUTE = 100 // Estimate for active time calculation

    init {
        loadData()
        startStepTracking()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Multi-user support: Load activities for current user only
            combine(
                userRepository.currentUserId,
                userRepository.stepGoal
            ) { userId, goal ->
                Triple(userId, goal, if (userId != null) activityRepository.getAllActivities(userId) else flowOf(emptyList()))
            }.flatMapLatest { (userId, goal, activitiesFlow) ->
                activitiesFlow.map { activities ->
                    Triple(userId, goal, activities)
                }
            }.collect { (userId, goal, activityList) ->
                _activities.value = activityList
                _stepGoal.value = goal
                updateStats(activityList)
                _isLoading.value = false
            }
        }
    }
    
    fun startStepTracking() {
        Log.d(TAG, "startStepTracking() called")
        stepSensorJob?.cancel()
        stepSensorJob = viewModelScope.launch {
            val trackingData = userRepository.stepTrackingData.first()
            var initialSensorSteps = -1
            var lastSavedStepsToday = trackingData.second
            var lastTrackingDate = trackingData.third
            var previousSensorSteps = trackingData.first

            stepSensor.stepFlow.collect { currentSensorSteps ->
                if (initialSensorSteps == -1) {
                    initialSensorSteps = currentSensorSteps
                }

                val today = Calendar.getInstance()
                // Reset to start of day for comparison
                today.set(Calendar.HOUR_OF_DAY, 0)
                today.set(Calendar.MINUTE, 0)
                today.set(Calendar.SECOND, 0)
                today.set(Calendar.MILLISECOND, 0)
                val todayDate = today.timeInMillis

                if (todayDate > lastTrackingDate) {
                    // New day!
                    // Save yesterday's data as an activity log if there were steps
                    if (lastSavedStepsToday > 0) {
                         // Estimate duration based on steps
                         val duration = lastSavedStepsToday / STEPS_PER_MINUTE
                         
                         // Multi-user: Get current userId
                         val userId = userRepository.currentUserId.first() ?: return@collect
                         
                         val yesterdayLog = ActivityLog(
                             userId = userId, // Set userId for multi-user support
                             activityType = "Walking (Daily)",
                             duration = duration,
                             calories = (lastSavedStepsToday * CALORIES_PER_STEP).toInt(),
                             distance = (lastSavedStepsToday * STRIDE_LENGTH_METERS) / 1000.0,
                             steps = lastSavedStepsToday,
                             date = lastTrackingDate
                         )
                         activityRepository.insertActivity(yesterdayLog)
                    }

                    // Reset for today
                    lastSavedStepsToday = 0
                    lastTrackingDate = todayDate
                    previousSensorSteps = currentSensorSteps
                    
                    // Update stored preferences
                    userRepository.saveStepTrackingData(currentSensorSteps, 0, todayDate)
                }
                
                // Calculate steps taken since last update
                val stepsSinceLastUpdate = if (currentSensorSteps >= previousSensorSteps) {
                    currentSensorSteps - previousSensorSteps
                } else {
                    // Sensor reset (e.g. reboot)
                    currentSensorSteps
                }
                
                if (stepsSinceLastUpdate > 0) {
                    lastSavedStepsToday += stepsSinceLastUpdate
                    previousSensorSteps = currentSensorSteps
                    
                    // For now, let's persist frequently to preferences so we don't lose data
                    userRepository.saveStepTrackingData(currentSensorSteps, lastSavedStepsToday, todayDate)
                    
                    // Update internal stats directly for responsiveness
                    Log.d(TAG, "Steps updated: liveSteps=$lastSavedStepsToday, sensorSteps=$currentSensorSteps")
                    updateLiveStats(lastSavedStepsToday)
                }
            }
        }
    }
    
    private fun updateLiveStats(liveSteps: Int) {
        val todaysActivities = _activities.value.filter { 
            val today = Calendar.getInstance()
            val activityDate = Calendar.getInstance().apply { timeInMillis = it.date }
            today.get(Calendar.YEAR) == activityDate.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == activityDate.get(Calendar.DAY_OF_YEAR)
        }

        val currentDbSteps = todaysActivities.sumOf { it.steps ?: 0 }
        val totalSteps = currentDbSteps + liveSteps
        _steps.value = totalSteps
        
        val currentDbCalories = todaysActivities.sumOf { it.calories }
        val liveCalories = (liveSteps * CALORIES_PER_STEP).toInt()
        _calories.value = currentDbCalories + liveCalories

        val currentDbDistance = todaysActivities.sumOf { it.distance }
        val liveDistance = (liveSteps * STRIDE_LENGTH_METERS) / 1000.0
        val totalDistance = currentDbDistance + liveDistance
        _distance.value = totalDistance
        
        val currentDbDuration = todaysActivities.sumOf { it.duration }
        val liveDuration = liveSteps / STEPS_PER_MINUTE
        val totalDuration = currentDbDuration + liveDuration
        _activeTime.value = totalDuration
        
        // Average Speed (km/h)
        if (totalDuration > 0) {
            _averageSpeed.value = totalDistance / (totalDuration / 60.0)
        } else {
            _averageSpeed.value = 0.0
        }
    }


    private fun updateStats(activities: List<ActivityLog>) {
        viewModelScope.launch {
            val trackingData = userRepository.stepTrackingData.first()
            val liveSteps = trackingData.second
            
            // This will recalculate all today stats including live steps
            updateLiveStats(liveSteps)
            
            // Calculate weekly average
            val sevenDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }.timeInMillis
            val recentActivities = activities.filter { it.date >= sevenDaysAgo }
            val totalStepsInWeek = recentActivities.sumOf { it.steps ?: 0 }
            
            // Include today's live steps in the weekly average
            val totalStepsWithLive = totalStepsInWeek + liveSteps
            
            _weeklyAvgSteps.value = if (recentActivities.isNotEmpty() || liveSteps > 0) totalStepsWithLive / 7 else 0
            
            // Calculate daily steps for current week (Monday to Sunday)
            val dailySteps = mutableListOf<Int>()
            val today = Calendar.getInstance()
            val currentDayOfWeek = today.get(Calendar.DAY_OF_WEEK)
            
            // Calculate how many days back to Monday
            val daysFromMonday = when (currentDayOfWeek) {
                Calendar.SUNDAY -> 6      // Sunday is 6 days from Monday
                Calendar.MONDAY -> 0      // Monday is 0 days from Monday
                Calendar.TUESDAY -> 1
                Calendar.WEDNESDAY -> 2
                Calendar.THURSDAY -> 3
                Calendar.FRIDAY -> 4
                Calendar.SATURDAY -> 5
                else -> 0
            }
            
            // Loop through Monday (0) to Sunday (6)
            for (dayOffset in 0..6) {
                val targetDay = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -daysFromMonday + dayOffset)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val dayEnd = Calendar.getInstance().apply {
                    timeInMillis = targetDay.timeInMillis
                    add(Calendar.DAY_OF_YEAR, 1)
                }
                
                val dayActivities = activities.filter { 
                    it.date >= targetDay.timeInMillis && it.date < dayEnd.timeInMillis 
                }
                val daySteps = dayActivities.sumOf { it.steps ?: 0 }
                
                // If this is today, add live steps
                val isToday = (dayOffset == daysFromMonday)
                if (isToday) {
                    dailySteps.add(daySteps + liveSteps)
                } else {
                    dailySteps.add(daySteps)
                }
            }
            
            _dailyStepsLast7Days.value = dailySteps
        }
    }

    fun addActivity(activity: ActivityLog) {
        viewModelScope.launch {
            // Multi-user: Ensure userId is set
            val userId = userRepository.currentUserId.first() ?: return@launch
            val activityWithUserId = activity.copy(userId = userId)
            Log.d(TAG, "addActivity() type=${activity.activityType}, duration=${activity.duration}, calories=${activity.calories}")
            activityRepository.insertActivity(activityWithUserId)
            Log.d(TAG, "addActivity() database insert success")
            _lastAddedActivity.value = activityWithUserId
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
    
    // Weekly Report Functions
    fun getWeeklyWorkoutDuration(weekOffset: Int = 0): Int {
        val activities = _activities.value
        val weekActivities = getActivitiesByWeek(weekOffset)
        return weekActivities.sumOf { it.duration }
    }
    
    fun getWeeklyAverageSteps(weekOffset: Int = 0): Int {
        val weekActivities = getActivitiesByWeek(weekOffset)
        val totalSteps = weekActivities.sumOf { it.steps ?: 0 }
        return if (weekActivities.isNotEmpty()) totalSteps / 7 else 0
    }
    
    fun getDailyActivities(date: Long): List<ActivityLog> {
        val activities = _activities.value
        val dayStart = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val dayEnd = Calendar.getInstance().apply {
            timeInMillis = dayStart.timeInMillis
            add(Calendar.DAY_OF_YEAR, 1)
        }
        
        return activities.filter {
            it.date >= dayStart.timeInMillis && it.date < dayEnd.timeInMillis
        }
    }
    
    fun getActivitiesByWeek(weekOffset: Int = 0): List<ActivityLog> {
        val activities = _activities.value
        val calendar = Calendar.getInstance()
        
        // Move to the target week
        calendar.add(Calendar.WEEK_OF_YEAR, weekOffset)
        
        // Get Monday of that week
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val daysFromMonday = when (dayOfWeek) {
            Calendar.SUNDAY -> 6
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            else -> 0
        }
        
        calendar.add(Calendar.DAY_OF_YEAR, -daysFromMonday)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        val weekStart = calendar.timeInMillis
        
        // Get Sunday of that week
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val weekEnd = calendar.timeInMillis
        
        return activities.filter {
            it.date >= weekStart && it.date < weekEnd
        }
    }
    
    fun getLongestActivityForDay(date: Long): ActivityLog? {
        val dayActivities = getDailyActivities(date)
        return dayActivities.maxByOrNull { it.duration }
    }
}

class ActivityViewModelFactory(
    private val application: Application,
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivityViewModel(application, activityRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}