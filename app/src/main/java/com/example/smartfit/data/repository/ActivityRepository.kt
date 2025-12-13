package com.example.smartfit.data.repository

import com.example.smartfit.data.database.ActivityDao
import com.example.smartfit.data.database.ActivityLog
import kotlinx.coroutines.flow.Flow

/**
 * Activity Repository - Multi-user support
 * All operations require userId for data isolation
 */
class ActivityRepository constructor(
    private val activityDao: ActivityDao
) {
    /**
     * Get all activities for a specific user
     */
    fun getAllActivities(userId: Int): Flow<List<ActivityLog>> = 
        activityDao.getAllActivities(userId)
    
    /**
     * Get activities by type for a specific user
     */
    fun getActivitiesByType(userId: Int, type: String): Flow<List<ActivityLog>> = 
        activityDao.getActivitiesByType(userId, type)
    
    /**
     * Get activity by ID for a specific user
     */
    suspend fun getActivityById(id: Long, userId: Int): ActivityLog? = 
        activityDao.getActivityById(id, userId)
    
    /**
     * Insert activity (userId must be set in ActivityLog object)
     */
    suspend fun insertActivity(activity: ActivityLog): Long = 
        activityDao.insertActivity(activity)
    
    /**
     * Update activity
     */
    suspend fun updateActivity(activity: ActivityLog) = 
        activityDao.updateActivity(activity)
    
    /**
     * Delete activity
     */
    suspend fun deleteActivity(activity: ActivityLog) = 
        activityDao.deleteActivity(activity)
    
    /**
     * Delete activity by ID for a specific user
     */
    suspend fun deleteActivityById(id: Long, userId: Int) = 
        activityDao.deleteActivityById(id, userId)
}