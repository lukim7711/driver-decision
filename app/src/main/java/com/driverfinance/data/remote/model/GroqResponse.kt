package com.driverfinance.data.remote.model

import com.google.gson.annotations.SerializedName

data class GroqResponse(
    val choices: List<GroqChoice>,
    val usage: GroqUsage?
)

data class GroqChoice(
    val message: GroqMessage
)

data class GroqUsage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int,
    @SerializedName("completion_tokens")
    val completionTokens: Int,
    @SerializedName("total_tokens")
    val totalTokens: Int
)
