package com.example.agenthire.data.api

import com.example.agenthire.data.models.GeminiRequest
import com.example.agenthire.data.models.GeminiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
    
    companion object {
        const val BASE_URL = "https://generativelanguage.googleapis.com/"
    }
} 