package com.example.smartfit.data.repository

import android.content.Context
import com.example.smartfit.data.database.User
import com.example.smartfit.data.database.UserCredentials
import com.example.smartfit.data.database.AppDatabase
import com.example.smartfit.data.datastore.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

class UserRepository(context: Context) {
    
    private val userPreferences = UserPreferences(context)
    private val userDao = AppDatabase.getDatabase(context).userDao()
    private val credentialsDao = AppDatabase.getDatabase(context).credentialsDao()

    // Multi-user support: Track current user ID
    val currentUserId: Flow<Int?> = userPreferences.currentUserIdFlow
    
    val theme: Flow<Boolean> = userPreferences.themeFlow.map { it ?: false }
    val stepGoal: Flow<Int> = userPreferences.stepGoalFlow

    val isLoggedIn: Flow<Boolean> = userPreferences.isLoggedInFlow

    suspend fun login() {
        userPreferences.login()
    }

    suspend fun logout() {
        userPreferences.clearCurrentUserId()
        userPreferences.logout()
    }
    
    val stepTrackingData: Flow<Triple<Int, Int, Long>> = userPreferences.stepTrackingFlow
    
    suspend fun saveStepTrackingData(lastStepCount: Int, savedStepsToday: Int, lastTrackingDate: Long) {
        userPreferences.saveStepTrackingData(lastStepCount, savedStepsToday, lastTrackingDate)
    }
    
    // ========== User Profile Methods (Multi-User) ==========
    
    /**
     * Get current user's profile (reactive)
     * Automatically switches when currentUserId changes
     */
    val userProfile: Flow<User?> = currentUserId.flatMapLatest { userId ->
        if (userId != null) {
            userDao.getUserProfile(userId)
        } else {
            flowOf(null)
        }
    }
    
    /**
     * Save or update user profile for current user
     */
    suspend fun saveUserProfile(name: String, age: Int, weight: Float, height: Float, gender: String) {
        val userId = userPreferences.currentUserIdFlow.map { it }.first() ?: return
        val now = System.currentTimeMillis()
        val existingUser = userDao.getUser(userId)
        
        val user = User(
            userId = userId,
            name = name,
            age = age,
            weight = weight,
            height = height,
            gender = gender,
            createdAt = existingUser?.createdAt ?: now,
            updatedAt = now
        )
        
        userDao.insertOrUpdateUser(user)
    }
    
    /**
     * Get current user's profile (one-time fetch)
     */
    suspend fun getUser(): User? {
        val userId = userPreferences.currentUserIdFlow.map { it }.first() ?: return null
        return userDao.getUser(userId)
    }
    
    // ========== Authentication Methods (Multi-User) ==========
    
    /**
     * Hash password using SHA-256
     * NOTE: For production, use BCrypt or Argon2 instead
     */
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Register new user
     * Creates credentials, profile, and auto-logs in
     * Returns userId on success, null on failure
     */
    suspend fun register(
        username: String,
        email: String,
        password: String,
        name: String,
        age: String,
        weight: String,
        height: String,
        gender: String
    ): Int? {
        return try {
            // Create credentials
            val credentials = UserCredentials(
                username = username,
                email = email,
                passwordHash = hashPassword(password)
            )
            val userId = credentialsDao.insert(credentials).toInt()
            
            // Create profile
            val now = System.currentTimeMillis()
            val user = User(
                userId = userId,
                name = name,
                age = age.toIntOrNull() ?: 25,
                weight = weight.toFloatOrNull() ?: 70f,
                height = height.toFloatOrNull() ?: 170f,
                gender = gender,
                createdAt = now,
                updatedAt = now
            )
            userDao.insertOrUpdateUser(user)
            
            // Auto-login
            userPreferences.setCurrentUserId(userId)
            userPreferences.login()
            
            userId
        } catch (e: Exception) {
            null // Registration failed (e.g., duplicate username/email)
        }
    }
    
    /**
     * Validate credentials and login
     * Returns true if successful, false otherwise
     */
    suspend fun validateAndLogin(usernameOrEmail: String, password: String): Boolean {
        val credentials = credentialsDao.findByUsernameOrEmail(usernameOrEmail) ?: return false
        val inputHash = hashPassword(password)
        
        return if (credentials.passwordHash == inputHash) {
            userPreferences.setCurrentUserId(credentials.id)
            userPreferences.login()
            true
        } else {
            false
        }
    }
    
    /**
     * Check if any users are registered
     */
    suspend fun isRegistered(): Boolean {
        return credentialsDao.getUserCount() > 0
    }
}