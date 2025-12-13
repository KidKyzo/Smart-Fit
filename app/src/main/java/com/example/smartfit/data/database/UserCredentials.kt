package com.example.smartfit.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User credentials entity for authentication
 * Stores username, email, and password hash
 * Single user app (id always = 1)
 */
@Entity(tableName = "user_credentials")
data class UserCredentials(
    @PrimaryKey val id: Int = 1, // Always 1 for single-user app
    val username: String,
    val email: String,
    val passwordHash: String, // SHA-256 hashed password
    val createdAt: Long = System.currentTimeMillis()
)
