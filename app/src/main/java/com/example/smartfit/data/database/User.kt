package com.example.smartfit.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * User profile entity - Multi-user support
 * Each profile is linked to a UserCredentials entry via userId
 */
@Entity(
    tableName = "user_profile",
    foreignKeys = [
        ForeignKey(
            entity = UserCredentials::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // Delete profile when user is deleted
        )
    ],
    indices = [Index(value = ["userId"], unique = true)] // One profile per user
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int, // Links to UserCredentials.id
    val name: String,
    val age: Int,
    val weight: Float, // in kg
    val height: Float, // in cm
    val gender: String = "Not specified",
    val createdAt: Long,
    val updatedAt: Long
)
