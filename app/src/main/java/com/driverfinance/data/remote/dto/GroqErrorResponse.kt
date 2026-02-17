package com.driverfinance.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Error response from Groq API.
 * Used to parse error body for user-friendly messages.
 */
data class GroqErrorResponse(
    @SerializedName("error")
    val error: GroqErrorDetail?
)

data class GroqErrorDetail(
    @SerializedName("message")
    val message: String?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("code")
    val code: String?
)
