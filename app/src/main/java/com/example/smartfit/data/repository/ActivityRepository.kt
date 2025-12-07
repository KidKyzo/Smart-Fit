package com.example.smartfit.data.repository

import com.example.smartfit.data.database.ActivityDao
import com.example.smartfit.data.database.ActivityLog
import kotlinx.coroutines.flow.Flow

class ActivityRepository constructor(
    private val activityDao: ActivityDao
) {
    fun getAllActivities(): Flow<List<ActivityLog>> = activityDao.getAllActivities()
    
    fun getActivitiesByType(type: String): Flow<List<ActivityLog>> = 
        activityDao.getActivitiesByType(type)
    
    suspend fun getActivityById(id: Long): ActivityLog? = activityDao.getActivityById(id)
    
    suspend fun insertActivity(activity: ActivityLog): Long = 
        activityDao.insertActivity(activity)
    
    suspend fun updateActivity(activity: ActivityLog) = 
        activityDao.updateActivity(activity)
    
    suspend fun deleteActivity(activity: ActivityLog) = 
        activityDao.deleteActivity(activity)
    
    suspend fun deleteActivityById(id: Long) = 
        activityDao.deleteActivityById(id)
}