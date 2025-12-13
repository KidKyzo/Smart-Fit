package com.example.smartfit.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Activity log entity - Multi-user support
 * Each activity is linked to a specific user via userId
 */
@Entity(
    tableName = "activity_logs",
    foreignKeys = [
        ForeignKey(
            entity = UserCredentials::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // Delete activities when user is deleted
        )
    ],
    indices = [Index(value = ["userId"])] // Index for faster queries
)
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Int, // Links to UserCredentials.id
    val activityType: String,
    val duration: Int, // in minutes
    val calories: Int,
    val distance: Double, // in km
    val steps: Int? = null,
    val date: Long, // timestamp
    val notes: String = ""
)