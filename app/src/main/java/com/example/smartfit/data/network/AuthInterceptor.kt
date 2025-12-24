package com.example.smartfit.data.network

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import java.lang.System

/**
 * OAuth 2.0 Authentication Interceptor
 * Automatically adds Bearer token to all API requests
 * Refreshes token when expired
 */
class AuthInterceptor(
    private val authService: FatSecretAuthService
) : Interceptor {
    
    private val TAG = "DebugSmartApp"
    private var cachedToken: String? = null
    private var tokenExpiry: Long = 0
    private val lock = ReentrantLock()
    
    override fun intercept(chain: Interceptor.Chain): Response {
        // Get valid token (refresh if needed)
        val token = try {
            getValidToken()
        } catch (e: IOException) {
            Log.e(TAG, "AuthInterceptor: Failed to get token - ${e.message}")
            throw e // Re-throw IOException for proper OkHttp handling
        }
        
        // Add Bearer token to request
        val authenticatedRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        
        return chain.proceed(authenticatedRequest)
    }
    
    @Throws(IOException::class)
    private fun getValidToken(): String {
        lock.withLock {
            // Check if token is expired or missing
            if (cachedToken == null || System.currentTimeMillis() >= tokenExpiry) {
                refreshToken()
            }
            return cachedToken ?: throw IOException("Failed to obtain access token")
        }
    }
    
    @Throws(IOException::class)
    private fun refreshToken() {
        runBlocking {
            try {
                Log.d(TAG, "AuthInterceptor: Refreshing access token...")
                val response = authService.getAccessToken()
                cachedToken = response.accessToken
                // Set expiry to 5 minutes before actual expiry for safety
                tokenExpiry = System.currentTimeMillis() + ((response.expiresIn - 300) * 1000)
                Log.d(TAG, "AuthInterceptor: Token refreshed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "AuthInterceptor: Token refresh failed - ${e.message}")
                cachedToken = null
                tokenExpiry = 0
                // new solution added: throw IOException for fixing the problem
                throw IOException("Failed to refresh access token: ${e.message}", e)
            }
        }
    }
}
