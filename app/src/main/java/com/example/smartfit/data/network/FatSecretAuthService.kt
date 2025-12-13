package com.example.smartfit.data.network

import com.example.smartfit.data.network.dto.TokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * FatSecret OAuth 2.0 Authentication Service
 * Used to obtain access tokens via Client Credentials grant
 */
interface FatSecretAuthService {
    
    @POST("connect/token")
    @FormUrlEncoded
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String = "client_credentials",
        @Field("scope") scope: String = "basic"
    ): TokenResponse
}
