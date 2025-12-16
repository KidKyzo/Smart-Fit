package com.example.smartfit.data.repository

import com.example.smartfit.data.network.ExerciseDbApi
import com.example.smartfit.data.network.dto.ExerciseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ExerciseRepository(private val api: ExerciseDbApi) {

    // Cache the list in memory so we don't download it every time
    private var cachedExercises: List<ExerciseDto> = emptyList()

    fun getExercises(bodyPart: String? = null): Flow<Result<List<ExerciseDto>>> = flow {
        try {
            if (cachedExercises.isEmpty()) {
                cachedExercises = api.getExercises()
            }

            // Perform filtering locally
            val result = if (bodyPart == null || bodyPart == "All") {
                cachedExercises
            } else {
                cachedExercises.filter { exercise ->
                    // Check if primary muscles contain the search term
                    exercise.primaryMuscles?.any {
                        it.contains(bodyPart, ignoreCase = true)
                    } == true
                }
            }
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}