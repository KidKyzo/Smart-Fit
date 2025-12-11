package com.example.smartfit.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User profile entity - Single user app (id always = 1)
 * Updates replace existing data to avoid duplication
 */
@Entity(tableName = "user_profile")
data class User(
    @PrimaryKey val id: Int = 1, // Always 1 for single-user app
    val name: String,
    val age: Int,
    val weight: Float, // in kg
    val height: Float, // in cm
    val gender: String = "Not specified",
    val createdAt: Long,
    val updatedAt: Long
)
