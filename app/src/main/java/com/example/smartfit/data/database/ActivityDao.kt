package com.example.smartfit.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity_logs ORDER BY date DESC")
    fun getAllActivities(): Flow<List<ActivityLog>>

    @Query("SELECT * FROM activity_logs WHERE id = :id")
    suspend fun getActivityById(id: Long): ActivityLog?

    @Query("SELECT * FROM activity_logs WHERE activityType = :type ORDER BY date DESC")
    fun getActivitiesByType(type: String): Flow<List<ActivityLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityLog): Long

    @Update
    suspend fun updateActivity(activity: ActivityLog)

    @Delete
    suspend fun deleteActivity(activity: ActivityLog)

    @Query("DELETE FROM activity_logs WHERE id = :id")
    suspend fun deleteActivityById(id: Long)
}