package com.example.smartfit.data.network.dto

import com.google.gson.annotations.SerializedName

/**
 * OAuth 2.0 Token Response from FatSecret
 */
data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int  // seconds (86400 = 24 hours)
)
