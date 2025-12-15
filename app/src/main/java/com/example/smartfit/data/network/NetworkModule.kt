package com.example.smartfit.data.network

import com.google.gson.GsonBuilder
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Network Module for FatSecret API
 * Provides configured Retrofit instances with OAuth 2.0 authentication
 */
object NetworkModule {
    
    private const val AUTH_BASE_URL = "https://oauth.fatsecret.com/"
    private const val API_BASE_URL = "https://platform.fatsecret.com/"

    private const val EXERCISE_DB_BASE_URL = "https://exercisedb.p.rapidapi.com/"
    
    // API Credentials
    private const val CLIENT_ID = "e46e44f2338c42b5aa17b8dc7a7f1f4c"
    private const val CLIENT_SECRET = "3c0d2a9a5c3f4f27a7ae591e824d21ad"


    // TODO: Replace with your NEW Key from ExerciseDB (subscribe to the Basic Plan first!)
    private const val NINJA_BASE_URL = "https://api.api-ninjas.com/"
    private const val NINJA_API_KEY = "mPlMTvvjm8DegZx1ns5QWw==9ylx4Y2OgkvNsfot" //
    
    /**
     * Provides FatSecret API instance with OAuth 2.0 authentication
     */
    fun provideFatSecretApi(): FatSecretApi {
        // Create auth service for obtaining tokens
        val authService = createAuthService()
        
        // Create auth interceptor for adding Bearer tokens
        val authInterceptor = AuthInterceptor(authService)
        
        // Create logging interceptor for debugging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        // Build OkHttp client with interceptors
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        // Build Gson with lenient parsing
        val gson = GsonBuilder()
            .setLenient()
            .create()
        
        // Build Retrofit for API calls
        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(FatSecretApi::class.java)
    }

    fun provideExerciseDbApi(): ExerciseDbApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-Api-Key", NINJA_API_KEY) // Note: Header is different
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(NINJA_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExerciseDbApi::class.java)
    }

    
    /**
     * Creates auth service for OAuth 2.0 token requests
     * Uses Basic Authentication with Client ID and Secret
     */
    private fun createAuthService(): FatSecretAuthService {
        // Create Basic Auth credentials
        val credentials = Credentials.basic(CLIENT_ID, CLIENT_SECRET)
        
        // Interceptor to add Basic Auth header
        val basicAuthInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", credentials)
                .build()
            chain.proceed(request)
        }
        
        // Logging interceptor
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        // Build OkHttp client for auth requests
        val client = OkHttpClient.Builder()
            .addInterceptor(basicAuthInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        
        // Build Retrofit for auth service
        return Retrofit.Builder()
            .baseUrl(AUTH_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FatSecretAuthService::class.java)
    }
}
