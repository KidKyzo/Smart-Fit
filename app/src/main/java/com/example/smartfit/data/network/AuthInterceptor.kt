package com.example.smartfit.data.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * OAuth 2.0 Authentication Interceptor
 * Automatically adds Bearer token to all API requests
 * Refreshes token when expired
 */
class AuthInterceptor(
    private val authService: FatSecretAuthService
) : Interceptor {
    
    private var cachedToken: String? = null
    private var tokenExpiry: Long = 0
    private val lock = ReentrantLock()
    
    override fun intercept(chain: Interceptor.Chain): Response {
        // Get valid token (refresh if needed)
        val token = getValidToken()
        
        // Add Bearer token to request
        val authenticatedRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        
        return chain.proceed(authenticatedRequest)
    }
    
    private fun getValidToken(): String {
        lock.withLock {
            // Check if token is expired or missing
            if (cachedToken == null || System.currentTimeMillis() >= tokenExpiry) {
                refreshToken()
            }
            return cachedToken ?: throw IllegalStateException("Failed to obtain access token")
        }
    }
    
    private fun refreshToken() {
        runBlocking {
            try {
                val response = authService.getAccessToken()
                cachedToken = response.accessToken
                // Set expiry to 5 minutes before actual expiry for safety
                tokenExpiry = System.currentTimeMillis() + ((response.expiresIn - 300) * 1000)
            } catch (e: Exception) {
                throw IllegalStateException("Failed to refresh access token", e)
            }
        }
    }
}
