package com.driverfinance.data.remote

import com.driverfinance.data.remote.model.GroqRequest
import com.driverfinance.data.remote.model.GroqResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GroqApiService {

    @POST("openai/v1/chat/completions")
    suspend fun chatCompletions(@Body request: GroqRequest): GroqResponse
}
