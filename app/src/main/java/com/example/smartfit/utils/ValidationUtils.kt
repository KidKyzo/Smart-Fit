package com.example.smartfit.utils

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String = ""
)

object ValidationUtils {
    fun validateName(name: String): ValidationResult {
        return if (name.isNotBlank()) {
            ValidationResult(true)
        } else {
            ValidationResult(false, "Name cannot be empty")
        }
    }

    fun validateAge(age: String): ValidationResult {
        val ageInt = age.toIntOrNull()
        return when {
            ageInt == null -> ValidationResult(false, "Invalid number")
            ageInt <= 0 -> ValidationResult(false, "Age must be positive")
            ageInt > 120 -> ValidationResult(false, "Age seems unlikely")
            else -> ValidationResult(true)
        }
    }

    fun validateWeight(weight: String): ValidationResult {
        val weightFloat = weight.toFloatOrNull()
        return when {
            weightFloat == null -> ValidationResult(false, "Invalid number")
            weightFloat <= 0 -> ValidationResult(false, "Weight must be positive")
            else -> ValidationResult(true)
        }
    }

    fun validateHeight(height: String): ValidationResult {
        val heightFloat = height.toFloatOrNull()
        return when {
            heightFloat == null -> ValidationResult(false, "Invalid number")
            heightFloat <= 0 -> ValidationResult(false, "Height must be positive")
            else -> ValidationResult(true)
        }
    }
}
