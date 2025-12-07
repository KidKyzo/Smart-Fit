package com.example.smartfit.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val activityType: String,
    val duration: Int, // in minutes
    val calories: Int,
    val distance: Double, // in km
    val steps: Int? = null,
    val date: Long, // timestamp
    val notes: String = ""
)