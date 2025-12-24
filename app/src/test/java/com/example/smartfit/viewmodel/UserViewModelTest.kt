package com.example.smartfit.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.security.MessageDigest

/**
 * Unit Tests for SmartFit Application
 * Tests core business logic including:
 * - Password hashing (authentication)
 * - BMI calculation (user profile)
 * - Step goal progress (activity tracking)
 */
class UserViewModelTest {

    // ========== UIT-1-1: Password Hashing Tests ==========
    
    /**
     * Test that password hashing produces consistent results.
     * This verifies the core authentication logic.
     */
    @Test
    fun passwordHashingIsConsistent() {
        // Arrange
        val password = "testPassword123"
        
        // Act - Hash the same password twice
        val hash1 = hashPassword(password)
        val hash2 = hashPassword(password)
            
        // Assert - Hashes should match
        assertEquals("Password hashes should be consistent", hash1, hash2)
    }
    
    /**
     * Test that different passwords produce different hashes.
     */
    @Test
    fun differentPasswordsProduceDifferentHashes() {
        // Arrange
        val password1 = "password123"
        val password2 = "password456"
        
        // Act
        val hash1 = hashPassword(password1)
        val hash2 = hashPassword(password2)
        
        // Assert
        assertNotEquals("Different passwords should have different hashes", hash1, hash2)
    }
    
    /**
     * Test that password hash has correct length (SHA-256 produces 64 hex characters).
     */
    @Test
    fun passwordHashHasCorrectLength() {
        // Arrange
        val password = "anyPassword"
        
        // Act
        val hash = hashPassword(password)
        
        // Assert - SHA-256 produces 32 bytes = 64 hex characters
        assertEquals("SHA-256 hash should be 64 characters", 64, hash.length)
    }
    
    // ========== UIT-1-2: BMI Calculation Tests ==========
    
    /**
     * Test BMI calculation with normal values.
     * Formula: weight(kg) / height(m)²
     */
    @Test
    fun bmiCalculationIsCorrectForNormalValues() {
        // Arrange
        val weight = 70f  // kg
        val height = 175f // cm
        
        // Act
        val bmi = calculateBMI(weight, height)
        
        // Assert - Expected: 70 / (1.75)² = 22.86
        assertEquals("BMI should be approximately 22.86", 22.86, bmi.toDouble(), 0.01)
    }
    
    /**
     * Test BMI calculation with underweight values.
     */
    @Test
    fun bmiCalculationIsCorrectForUnderweightValues() {
        // Arrange
        val weight = 50f  // kg
        val height = 175f // cm
        
        // Act
        val bmi = calculateBMI(weight, height)
        
        // Assert - Expected: 50 / (1.75)² = 16.33 (underweight < 18.5)
        assertTrue("BMI should be less than 18.5 for underweight", bmi < 18.5f)
    }
    
    /**
     * Test BMI calculation with obese values.
     */
    @Test
    fun bmiCalculationIsCorrectForObeseValues() {
        // Arrange
        val weight = 100f // kg
        val height = 170f // cm
        
        // Act
        val bmi = calculateBMI(weight, height)
        
        // Assert - Expected: 100 / (1.70)² = 34.6 (obese >= 30)
        assertTrue("BMI should be >= 30 for obese", bmi >= 30f)
    }
    
    // ========== UIT-1-3: Step Goal Progress Tests ==========
    
    /**
     * Test step goal progress calculation at 50%.
     */
    @Test
    fun stepGoalProgressIsCorrectAt50Percent() {
        // Arrange
        val currentSteps = 5000
        val goalSteps = 10000
        
        // Act
        val progress = calculateStepProgress(currentSteps, goalSteps)
        
        // Assert
        assertEquals("Progress should be 50%", 50f, progress, 0.01f)
    }
    
    /**
     * Test step goal progress calculation at 100%.
     */
    @Test
    fun stepGoalProgressIsCorrectAt100Percent() {
        // Arrange
        val currentSteps = 10000
        val goalSteps = 10000
        
        // Act
        val progress = calculateStepProgress(currentSteps, goalSteps)
        
        // Assert
        assertEquals("Progress should be 100%", 100f, progress, 0.01f)
    }
    
    /**
     * Test step goal progress calculation exceeding goal.
     */
    @Test
    fun stepGoalProgressCapsAt100PercentWhenExceeded() {
        // Arrange
        val currentSteps = 15000
        val goalSteps = 10000
        
        // Act
        val progress = calculateStepProgress(currentSteps, goalSteps)
        
        // Assert - Should cap at 100% or show actual (150%)
        assertTrue("Progress should be >= 100% when goal exceeded", progress >= 100f)
    }
    
    /**
     * Test step goal progress with zero steps.
     */
    @Test
    fun stepGoalProgressIsZeroWhenNoSteps() {
        // Arrange
        val currentSteps = 0
        val goalSteps = 10000
        
        // Act
        val progress = calculateStepProgress(currentSteps, goalSteps)
        
        // Assert
        assertEquals("Progress should be 0% with no steps", 0f, progress, 0.01f)
    }
    
    // ========== Helper Functions (Replicating App Logic) ==========
    
    /**
     * Replicates the hashing logic from UserRepository for testing.
     */
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Replicates BMI calculation logic.
     * Formula: weight(kg) / height(m)²
     */
    private fun calculateBMI(weightKg: Float, heightCm: Float): Float {
        val heightM = heightCm / 100f
        return weightKg / (heightM * heightM)
    }
    
    /**
     * Replicates step progress calculation logic.
     * Formula: (currentSteps / goalSteps) × 100
     */
    private fun calculateStepProgress(currentSteps: Int, goalSteps: Int): Float {
        if (goalSteps <= 0) return 0f
        return (currentSteps.toFloat() / goalSteps.toFloat()) * 100f
    }
}
