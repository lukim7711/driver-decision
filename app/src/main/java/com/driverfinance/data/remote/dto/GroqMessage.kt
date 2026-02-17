package com.driverfinance.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * A single message in the Groq chat conversation.
 *
 * Roles:
 * - "system"    → System prompt + driver data context
 * - "user"      → Driver's message
 * - "assistant" → AI response
 */
data class GroqMessage(
    @SerializedName("role")
    val role: String,

    @SerializedName("content")
    val content: String
) {
    companion object {
        const val ROLE_SYSTEM = "system"
        const val ROLE_USER = "user"
        const val ROLE_ASSISTANT = "assistant"
    }
}
