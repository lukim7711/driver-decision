package com.driverfinance.data.remote

import com.driverfinance.data.remote.dto.GroqChatRequest
import com.driverfinance.data.remote.dto.GroqChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit interface for Groq LLM API.
 *
 * Endpoint: https://api.groq.com/openai/v1/chat/completions
 * Auth: Bearer token via AuthInterceptor
 * Model: llama-3.3-70b-versatile
 * Rate limit: 30 req/min (free tier)
 *
 * Used by: F008 (AI Chat), F003 (Pattern Generation, optional)
 */
interface GroqApiService {

    @POST("openai/v1/chat/completions")
    suspend fun chatCompletion(
        @Body request: GroqChatRequest
    ): Response<GroqChatResponse>
}
