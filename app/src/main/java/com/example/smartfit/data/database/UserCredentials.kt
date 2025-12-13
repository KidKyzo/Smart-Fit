package com.example.smartfit.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User credentials entity for authentication
 * Stores username, email, and password hash
 * Multi-user support: Each user gets a unique auto-generated ID
 */
@Entity(tableName = "user_credentials")
data class UserCredentials(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Auto-increment for multi-user
    val username: String,
    val email: String,
    val passwordHash: String, // SHA-256 hashed password
    val createdAt: Long = System.currentTimeMillis()
)
