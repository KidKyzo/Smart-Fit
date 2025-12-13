package com.example.smartfit.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO for user credentials operations - Multi-user support
 */
@Dao
interface CredentialsDao {
    
    /**
     * Insert new credentials and return the generated userId
     * ABORT strategy prevents duplicate usernames/emails
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(credentials: UserCredentials): Long
    
    /**
     * Find credentials by username or email
     * Used for login validation
     */
    @Query("SELECT * FROM user_credentials WHERE username = :usernameOrEmail OR email = :usernameOrEmail LIMIT 1")
    suspend fun findByUsernameOrEmail(usernameOrEmail: String): UserCredentials?
    
    /**
     * Get credentials by ID
     */
    @Query("SELECT * FROM user_credentials WHERE id = :userId")
    suspend fun getCredentialsById(userId: Int): UserCredentials?
    
    /**
     * Check if any users exist
     */
    @Query("SELECT COUNT(*) FROM user_credentials")
    suspend fun getUserCount(): Int
    
    /**
     * Get all users (for admin/debugging)
     */
    @Query("SELECT * FROM user_credentials ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserCredentials>>
}
