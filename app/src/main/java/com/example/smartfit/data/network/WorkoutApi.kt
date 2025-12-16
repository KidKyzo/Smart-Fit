package com.example.smartfit.data.network

import com.example.smartfit.data.network.dto.ExerciseDto
import retrofit2.http.GET

interface ExerciseDbApi {
    // Fetches the entire database at once (approx 800+ exercises)
    // Path: yuhonas/free-exercise-db/main/dist/exercises.json
    @GET("yuhonas/free-exercise-db/main/dist/exercises.json")
    suspend fun getExercises(): List<ExerciseDto>
}