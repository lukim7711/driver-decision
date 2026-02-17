package com.driverfinance.domain.usecase.chat

import com.driverfinance.data.remote.dto.ChatMessage
import com.driverfinance.data.repository.ChatContextBuilder
import com.driverfinance.data.repository.ChatRepository
import com.driverfinance.data.repository.ChatResult
import javax.inject.Inject

/**
 * Sends chat message with context injection and conversation history.
 * Per F008 §6.3: system prompt + context data + conversation memory.
 */
class SendChatMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val chatContextBuilder: ChatContextBuilder
) {

    companion object {
        private const val MAX_HISTORY_MESSAGES = 20

        private val SYSTEM_PROMPT = """
            Kamu adalah AI Financial Advisor untuk Shopee Driver.
            Kamu HANYA menjawab pertanyaan seputar data kerja dan keuangan driver.
            Kamu TIDAK menjawab pertanyaan umum, tidak cari info di internet, tidak bahas politik, olahraga, cuaca, atau topik lain.
            
            Aturan jawaban:
            - Bahasa Indonesia sehari-hari, singkat, dan actionable
            - Angka penting ditulis bold: **Rp150.000**
            - Emoji secukupnya (tidak berlebihan)
            - Maksimal ~150 kata per jawaban
            - Untuk data list, gunakan numbered list
            - Selalu akhiri dengan insight/saran actionable jika relevan
            
            Jika driver tidak menyebut waktu spesifik:
            - Jika ada data hari ini → default jawab hari ini
            - Jika belum ada data hari ini → jawab berdasarkan data terakhir yang tersedia
            - Untuk pertanyaan tentang tren/perbandingan → gunakan minggu ini
            - Untuk pertanyaan tentang hutang/target → gunakan bulan ini
            
            Jika pertanyaan di luar scope:
            "Saya cuma bisa bantu soal data kerja dan keuangan kamu ya \uD83D\uDE0A Coba tanya tentang performa, hutang, target, atau keuangan kamu."
            
            Jika pertanyaan tentang fitur belum tersedia (analisa area, prediksi, simulasi):
            "Fitur ini belum tersedia sekarang, tapi sedang direncanakan ya \uD83D\uDE0A"
            
            Jika belum ada data sama sekali:
            "Belum ada data yang bisa saya analisa. Mulai narik dulu, nanti saya bantu analisa!"
        """.trimIndent()
    }

    suspend operator fun invoke(
        userMessage: String,
        conversationHistory: List<ChatMessage>
    ): ChatMessageResult {
        // Build context from all feature data
        val driverContext = chatContextBuilder.buildContext()

        // Combine system prompt with context
        val fullSystemPrompt = """
            $SYSTEM_PROMPT
            
            === DATA DRIVER ===
            $driverContext
        """.trimIndent()

        // Trim history to max 20 messages
        val trimmedHistory = conversationHistory.takeLast(MAX_HISTORY_MESSAGES)

        // Send to LLM
        return when (val result = chatRepository.sendChat(fullSystemPrompt, trimmedHistory, userMessage)) {
            is ChatResult.Success -> ChatMessageResult.Success(result.message)
            is ChatResult.NoInternet -> ChatMessageResult.NoInternet
            is ChatResult.Timeout -> ChatMessageResult.Timeout
            is ChatResult.Error -> ChatMessageResult.Error(result.message)
        }
    }
}

sealed interface ChatMessageResult {
    data class Success(val aiResponse: String) : ChatMessageResult
    data object NoInternet : ChatMessageResult
    data object Timeout : ChatMessageResult
    data class Error(val message: String) : ChatMessageResult
}
