package com.example.smartfit.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for User profile operations - Multi-user support
 * Each user profile is linked to credentials via userId
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
     * Get user profile by userId as Flow (reactive updates)
     * Returns null if no profile exists for this user
     */
    @Query("SELECT * FROM user_profile WHERE userId = :userId LIMIT 1")
    fun getUserProfile(userId: Int): Flow<User?>
    
    /**
     * Get user profile by userId (one-time fetch)
     */
    @Query("SELECT * FROM user_profile WHERE userId = :userId LIMIT 1")
    suspend fun getUser(userId: Int): User?
    
    /**
     * Delete user profile by userId
     */
    @Query("DELETE FROM user_profile WHERE userId = :userId")
    suspend fun deleteUserProfile(userId: Int)
    
    /**
     * Get all user profiles (for admin/debugging)
     */
    @Query("SELECT * FROM user_profile ORDER BY createdAt DESC")
    fun getAllProfiles(): Flow<List<User>>
}
