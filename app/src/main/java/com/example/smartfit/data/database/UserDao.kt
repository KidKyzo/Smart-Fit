package com.example.smartfit.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for User profile operations
 * Uses REPLACE strategy to avoid duplication
 */
@Dao
interface UserDao {
    
    /**
     * Insert or update user profile
     * OnConflictStrategy.REPLACE ensures old data is replaced, not duplicated
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: User)
    
    /**
     * Get user profile as Flow (reactive updates)
     * Returns null if no user exists
     */
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<User?>
    
    /**
     * Get user profile (one-time fetch)
     */
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUser(): User?
    
    /**
     * Delete user profile (for testing/reset)
     */
    @Query("DELETE FROM user_profile")
    suspend fun deleteUser()
}
