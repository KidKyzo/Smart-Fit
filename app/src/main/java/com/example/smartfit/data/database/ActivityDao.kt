package com.example.smartfit.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Activity operations - Multi-user support
 * All queries filter by userId to ensure data isolation
 */
@Dao
interface ActivityDao {
    /**
     * Get all activities for a specific user
     */
    @Query("SELECT * FROM activity_logs WHERE userId = :userId ORDER BY date DESC")
    fun getAllActivities(userId: Int): Flow<List<ActivityLog>>

    /**
     * Get activity by ID (still needs userId for security)
     */
    @Query("SELECT * FROM activity_logs WHERE id = :id AND userId = :userId")
    suspend fun getActivityById(id: Long, userId: Int): ActivityLog?

    /**
     * Get activities by type for a specific user
     */
    @Query("SELECT * FROM activity_logs WHERE userId = :userId AND activityType = :type ORDER BY date DESC")
    fun getActivitiesByType(userId: Int, type: String): Flow<List<ActivityLog>>

    /**
     * Insert activity (userId must be set in the ActivityLog object)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityLog): Long

    /**
     * Update activity
     */
    @Update
    suspend fun updateActivity(activity: ActivityLog)

    /**
     * Delete activity
     */
    @Delete
    suspend fun deleteActivity(activity: ActivityLog)

    /**
     * Delete activity by ID (with userId check for security)
     */
    @Query("DELETE FROM activity_logs WHERE id = :id AND userId = :userId")
    suspend fun deleteActivityById(id: Long, userId: Int)
    
    /**
     * Get all activities for all users (admin/debugging only)
     */
    @Query("SELECT * FROM activity_logs ORDER BY date DESC")
    fun getAllActivitiesAllUsers(): Flow<List<ActivityLog>>
}