package com.driverfinance.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request body for Groq chat/completions API.
 * Ref: ARCHITECTURE.md Section 3.1
 *
 * Model: llama-3.3-70b-versatile
 * Max tokens: 300 (F008 spec Section 6.3 #9)
 * Temperature: 0.7
 */
data class GroqChatRequest(
    @SerializedName("model")
    val model: String = MODEL_DEFAULT,

    @SerializedName("messages")
    val messages: List<GroqMessage>,

    @SerializedName("max_tokens")
    val maxTokens: Int = MAX_TOKENS_DEFAULT,

    @SerializedName("temperature")
    val temperature: Double = TEMPERATURE_DEFAULT
) {
    companion object {
        const val MODEL_DEFAULT = "llama-3.3-70b-versatile"
        const val MAX_TOKENS_DEFAULT = 300
        const val TEMPERATURE_DEFAULT = 0.7
    }
}
