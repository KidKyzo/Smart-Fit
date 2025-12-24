package com.example.smartfit.data.repository

import com.example.smartfit.data.network.ExerciseDbApi
import com.example.smartfit.data.network.dto.ExerciseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ExerciseRepository(private val api: ExerciseDbApi) {

    private var cachedExercises: List<ExerciseDto> = emptyList()

    fun getExercises(bodyPart: String? = null): Flow<Result<List<ExerciseDto>>> = flow {
        try {
            if (cachedExercises.isEmpty()) {
                cachedExercises = api.getExercises()
            }

            val result = if (bodyPart == null || bodyPart == "All") {
                cachedExercises
            } else {
                cachedExercises.filter { exercise ->
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
