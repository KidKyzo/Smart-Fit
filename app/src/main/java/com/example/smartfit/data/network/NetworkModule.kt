package com.example.smartfit.data.network

import com.google.gson.GsonBuilder
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    
    private const val AUTH_BASE_URL = "https://oauth.fatsecret.com/"
    private const val API_BASE_URL = "https://platform.fatsecret.com/"

    private const val EXERCISE_DB_BASE_URL = "https://exercisedb.p.rapidapi.com/"
    
    private const val CLIENT_ID = "e46e44f2338c42b5aa17b8dc7a7f1f4c"
    private const val CLIENT_SECRET = "3c0d2a9a5c3f4f27a7ae591e824d21ad"

    private const val GITHUB_BASE_URL = "https://raw.githubusercontent.com/"
    private const val NINJA_API_KEY = "mPlMTvvjm8DegZx1ns5QWw==9ylx4Y2OgkvNsfot"
    
    fun provideFatSecretApi(): FatSecretApi {
        val authService = createAuthService()
        
        val authInterceptor = AuthInterceptor(authService)
        
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        val gson = GsonBuilder()
            .setLenient()
            .create()
        
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

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        return Retrofit.Builder()
            .baseUrl(GITHUB_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExerciseDbApi::class.java)
    }

    
    private fun createAuthService(): FatSecretAuthService {
        val credentials = Credentials.basic(CLIENT_ID, CLIENT_SECRET)
        
        val basicAuthInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", credentials)
                .build()
            chain.proceed(request)
        }
        
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(basicAuthInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        
        return Retrofit.Builder()
            .baseUrl(AUTH_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FatSecretAuthService::class.java)
    }
}
