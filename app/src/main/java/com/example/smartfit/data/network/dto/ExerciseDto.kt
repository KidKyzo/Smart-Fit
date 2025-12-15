package com.example.smartfit.data.network.dto

import com.google.gson.annotations.SerializedName

data class ExerciseDto(
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String?,
    @SerializedName("muscle") val muscle: String?,
    @SerializedName("equipment") val equipment: String?,
    @SerializedName("difficulty") val difficulty: String?,
    @SerializedName("instructions") val instructions: String?
)