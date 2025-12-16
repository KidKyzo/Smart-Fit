package com.example.smartfit.data.network.dto

import com.google.gson.annotations.SerializedName

data class ExerciseDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("force") val force: String?,
    @SerializedName("level") val level: String?,
    @SerializedName("mechanic") val mechanic: String?,
    @SerializedName("equipment") val equipment: String?,
    @SerializedName("primaryMuscles") val primaryMuscles: List<String>?,
    @SerializedName("secondaryMuscles") val secondaryMuscles: List<String>?,
    @SerializedName("instructions") val instructions: List<String>?,
    @SerializedName("category") val category: String?,
    @SerializedName("images") val images: List<String>?
)