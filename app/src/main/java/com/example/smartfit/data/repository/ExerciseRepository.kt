package com.example.smartfit.data.repository

import com.example.smartfit.data.network.ExerciseDbApi
import com.example.smartfit.data.network.dto.ExerciseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ExerciseRepository(private val api: ExerciseDbApi) {

    fun getExercises(bodyPart: String? = null): Flow<Result<List<ExerciseDto>>> = flow {
        try {
            // API Ninjas requires the muscle name to be exact and lowercase
            val query = if (bodyPart == "All") null else bodyPart?.lowercase()

            val result = api.getExercises(muscle = query)
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}