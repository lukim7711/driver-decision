package com.driverfinance.data.repository

import com.driverfinance.data.remote.GroqApiService
import com.driverfinance.data.remote.NetworkResult
import com.driverfinance.data.remote.dto.GroqChatRequest
import com.driverfinance.data.remote.dto.GroqChatResponse
import com.driverfinance.data.remote.dto.GroqErrorResponse
import com.driverfinance.data.remote.dto.GroqMessage
import com.google.gson.Gson
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for F008 AI Chat.
 *
 * Responsibilities:
 * - Manage in-memory conversation history (max 20 messages)
 * - Build system prompt + driver data context
 * - Send messages to Groq API
 * - Parse responses and errors
 *
 * Conversation history is in-memory only (cleared on navigation away).
 * Ref: F008 spec Section 6.3 #3
 */
@Singleton
class ChatRepository @Inject constructor(
    private val groqApiService: GroqApiService
) {

    companion object {
        private const val MAX_HISTORY_SIZE = 20

        val SYSTEM_PROMPT = """
            Kamu adalah AI Financial Advisor untuk Shopee Driver.
            Kamu HANYA menjawab pertanyaan seputar data kerja dan keuangan driver.
            Kamu TIDAK menjawab pertanyaan umum, tidak cari info di internet.
            Jawab dalam bahasa Indonesia sehari-hari, singkat, dan actionable.
            Gunakan emoji secukupnya.
            Angka penting ditulis bold (**Rp150.000**).
            Maksimal 150 kata per jawaban.

            Jika driver tidak menyebut waktu spesifik:
            - Jika ada data hari ini → default jawab hari ini
            - Jika belum ada data hari ini → jawab berdasarkan data terakhir yang tersedia
            - Untuk pertanyaan tentang tren/perbandingan → gunakan minggu ini
            - Untuk pertanyaan tentang hutang/target → gunakan bulan ini

            Jika driver tanya di luar scope, jawab:
            "Saya cuma bisa bantu soal data kerja dan keuangan kamu ya \uD83D\uDE0A
            Coba tanya tentang:
            • Performa hari/minggu/bulan
            • Progress hutang
            • Target harian
            • Pengeluaran
            • Saran keuangan"
        """.trimIndent()
    }

    private val conversationHistory = mutableListOf<GroqMessage>()

    /**
     * Send a message to Groq API with driver data context.
     *
     * @param userMessage The driver's chat message
     * @param driverDataContext Compiled context string from local database
     * @return NetworkResult wrapping the AI response
     */
    suspend fun sendMessage(
        userMessage: String,
        driverDataContext: String
    ): NetworkResult<String> {
        val userMsg = GroqMessage(
            role = GroqMessage.ROLE_USER,
            content = userMessage
        )
        conversationHistory.add(userMsg)
        trimHistory()

        val systemMessage = GroqMessage(
            role = GroqMessage.ROLE_SYSTEM,
            content = "$SYSTEM_PROMPT\n\n$driverDataContext"
        )

        val allMessages = mutableListOf(systemMessage)
        allMessages.addAll(conversationHistory)

        val request = GroqChatRequest(
            messages = allMessages
        )

        return try {
            val response = groqApiService.chatCompletion(request)

            if (response.isSuccessful) {
                val body = response.body()
                val content = body?.getContent().orEmpty()

                if (content.isNotBlank()) {
                    val assistantMsg = GroqMessage(
                        role = GroqMessage.ROLE_ASSISTANT,
                        content = content
                    )
                    conversationHistory.add(assistantMsg)
                    trimHistory()

                    Timber.d("Groq response: tokens=%d", body?.usage?.totalTokens ?: 0)
                    NetworkResult.Success(content)
                } else {
                    Timber.w("Groq returned empty content")
                    NetworkResult.Error(response.code(), "AI tidak bisa memproses. Coba lagi.")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody, response.code())
                Timber.w("Groq API error: %d - %s", response.code(), errorMessage)
                NetworkResult.Error(response.code(), errorMessage)
            }
        } catch (e: IOException) {
            Timber.e(e, "Network error calling Groq API")
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error calling Groq API")
            NetworkResult.Exception(e)
        }
    }

    fun clearHistory() {
        conversationHistory.clear()
    }

    fun getHistorySize(): Int = conversationHistory.size

    private fun trimHistory() {
        while (conversationHistory.size > MAX_HISTORY_SIZE) {
            conversationHistory.removeAt(0)
        }
    }

    private fun parseErrorMessage(errorBody: String?, httpCode: Int): String {
        return try {
            if (!errorBody.isNullOrBlank()) {
                val parsed = Gson().fromJson(errorBody, GroqErrorResponse::class.java)
                when {
                    httpCode == 429 -> "Terlalu banyak permintaan. Tunggu sebentar ya."
                    httpCode in 500..599 -> "Ada gangguan, coba beberapa saat lagi."
                    else -> parsed?.error?.message ?: "Gagal memproses. Coba lagi."
                }
            } else {
                getDefaultErrorMessage(httpCode)
            }
        } catch (e: Exception) {
            getDefaultErrorMessage(httpCode)
        }
    }

    private fun getDefaultErrorMessage(httpCode: Int): String {
        return when (httpCode) {
            401 -> "API key tidak valid."
            429 -> "Terlalu banyak permintaan. Tunggu sebentar ya."
            in 500..599 -> "Ada gangguan, coba beberapa saat lagi."
            else -> "Gagal memproses. Coba lagi."
        }
    }
}
