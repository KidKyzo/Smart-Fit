package com.example.smartfit.data.repository

import android.content.Context
import com.example.smartfit.data.database.User
import com.example.smartfit.data.database.UserCredentials
import com.example.smartfit.data.database.AppDatabase
import com.example.smartfit.data.datastore.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

class UserRepository(context: Context) {
    
    private val userPreferences = UserPreferences(context)
    private val userDao = AppDatabase.getDatabase(context).userDao()
    private val credentialsDao = AppDatabase.getDatabase(context).credentialsDao()

    val theme: Flow<Boolean> = userPreferences.themeFlow.map { it ?: false }
    val stepGoal: Flow<Int> = userPreferences.stepGoalFlow

    val isLoggedIn: Flow<Boolean> = userPreferences.isLoggedInFlow

    suspend fun login() {
        userPreferences.login()
    }

    suspend fun logout() {
        userPreferences.logout()
    }
    
    val stepTrackingData: Flow<Triple<Int, Int, Long>> = userPreferences.stepTrackingFlow
    
    suspend fun saveStepTrackingData(lastStepCount: Int, savedStepsToday: Int, lastTrackingDate: Long) {
        userPreferences.saveStepTrackingData(lastStepCount, savedStepsToday, lastTrackingDate)
    }
    
    // User Profile Methods
    val userProfile: Flow<User?> = userDao.getUserProfile()
    
    /**
     * Save or update user profile
     * Uses REPLACE strategy - old data is replaced, not duplicated
     */
    suspend fun saveUserProfile(name: String, age: Int, weight: Float, height: Float, gender: String) {
        val now = System.currentTimeMillis()
        val existingUser = userDao.getUser()
        
        val user = User(
            id = 1, // Always 1 for single-user app
            name = name,
            age = age,
            weight = weight,
            height = height,
            gender = gender,
            createdAt = existingUser?.createdAt ?: now,
            updatedAt = now
        )
        
        userDao.insertOrUpdateUser(user) // REPLACE strategy prevents duplication
    }
    
    suspend fun getUser(): User? = userDao.getUser()
    
    // ========== Authentication Methods ==========
    
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
     * Save user credentials
     */
    suspend fun saveCredentials(username: String, email: String, password: String) {
        val credentials = UserCredentials(
            username = username,
            email = email,
            passwordHash = hashPassword(password)
        )
        credentialsDao.saveCredentials(credentials)
    }
    
    /**
     * Validate user credentials
     */
    suspend fun validateCredentials(usernameOrEmail: String, password: String): Boolean {
        val credentials = credentialsDao.findByUsernameOrEmail(usernameOrEmail) ?: return false
        val inputHash = hashPassword(password)
        return credentials.passwordHash == inputHash
    }
    
    /**
     * Check if user is registered
     */
    suspend fun isRegistered(): Boolean {
        return credentialsDao.hasCredentials() > 0
    }
}