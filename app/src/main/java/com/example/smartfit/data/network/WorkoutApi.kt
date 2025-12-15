package com.example.smartfit.data.network

import com.example.smartfit.data.network.dto.ExerciseDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ExerciseDbApi {
    @GET("v1/exercises")
    suspend fun getExercises(
        @Query("muscle") muscle: String? = null,
        @Query("name") name: String? = null
    ): List<ExerciseDto>
}