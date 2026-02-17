package com.driverfinance.data.remote.model

import com.google.gson.annotations.SerializedName

data class GroqRequest(
    val model: String = "llama-3.3-70b-versatile",
    val messages: List<GroqMessage>,
    @SerializedName("max_tokens")
    val maxTokens: Int = 300,
    val temperature: Double = 0.7
)

data class GroqMessage(
    val role: String,
    val content: String
)
