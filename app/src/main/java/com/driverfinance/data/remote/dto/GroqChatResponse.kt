package com.driverfinance.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response from Groq chat/completions API.
 * Ref: ARCHITECTURE.md Section 3.1
 */
data class GroqChatResponse(
    @SerializedName("id")
    val id: String?,

    @SerializedName("choices")
    val choices: List<GroqChoice>,

    @SerializedName("usage")
    val usage: GroqUsage?
) {
    fun getContent(): String {
        return choices.firstOrNull()?.message?.content.orEmpty()
    }
}

data class GroqChoice(
    @SerializedName("index")
    val index: Int,

    @SerializedName("message")
    val message: GroqMessage,

    @SerializedName("finish_reason")
    val finishReason: String?
)

data class GroqUsage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int,

    @SerializedName("completion_tokens")
    val completionTokens: Int,

    @SerializedName("total_tokens")
    val totalTokens: Int
)
