package com.example.smartfit.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO for user credentials operations
 */
@Dao
interface CredentialsDao {
    
    /**
     * Get stored credentials (single user)
     */
    @Query("SELECT * FROM user_credentials WHERE id = 1")
    fun getCredentials(): Flow<UserCredentials?>
    
    /**
     * Find credentials by username or email
     * Used for login validation
     */
    @Query("SELECT * FROM user_credentials WHERE username = :usernameOrEmail OR email = :usernameOrEmail LIMIT 1")
    suspend fun findByUsernameOrEmail(usernameOrEmail: String): UserCredentials?
    
    /**
     * Save or update credentials
     * Replaces existing data (no duplication)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCredentials(credentials: UserCredentials)
    
    /**
     * Check if credentials exist
     */
    @Query("SELECT COUNT(*) FROM user_credentials")
    suspend fun hasCredentials(): Int
}
