package com.example.smartfit.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Food Intake Log Entity
 * Tracks foods consumed by users with multi-user support
 */
@Entity(
    tableName = "food_intake_logs",
    foreignKeys = [
        ForeignKey(
            entity = UserCredentials::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"]), Index(value = ["date"])]
)
data class FoodIntakeLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Int,  // Links to UserCredentials.id
    val foodId: String,  // FatSecret food_id
    val foodName: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val servingSize: String,
    val servings: Float = 1.0f,  // Number of servings consumed
    val date: Long,  // Timestamp when consumed
    val mealType: String = "Snack"  // Breakfast, Lunch, Dinner, Snack
)
