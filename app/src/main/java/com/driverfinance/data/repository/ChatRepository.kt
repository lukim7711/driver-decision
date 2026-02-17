package com.driverfinance.data.repository

import com.driverfinance.data.remote.GroqApiService
import com.driverfinance.data.remote.dto.ChatCompletionRequest
import com.driverfinance.data.remote.dto.ChatMessage
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles communication with Groq LLM API for chat completions.
 * Uses existing Groq API setup from PR #16.
 */
@Singleton
class ChatRepository @Inject constructor(
    private val groqApiService: GroqApiService
) {

    suspend fun sendChat(
        systemPrompt: String,
        conversationHistory: List<ChatMessage>,
        userMessage: String
    ): ChatResult {
        return try {
            val messages = buildList {
                add(ChatMessage(role = "system", content = systemPrompt))
                addAll(conversationHistory)
                add(ChatMessage(role = "user", content = userMessage))
            }

            val request = ChatCompletionRequest(
                model = "llama-3.3-70b-versatile",
                messages = messages,
                maxTokens = 300,
                temperature = 0.7
            )

            val response = groqApiService.chatCompletion(request)
            val aiMessage = response.choices.firstOrNull()?.message?.content
                ?: "Maaf, saya tidak bisa memproses pertanyaan kamu saat ini."

            ChatResult.Success(aiMessage)
        } catch (e: java.net.UnknownHostException) {
            ChatResult.NoInternet
        } catch (e: java.net.SocketTimeoutException) {
            ChatResult.Timeout
        } catch (e: Exception) {
            ChatResult.Error(e.message ?: "Unknown error")
        }
    }
}

sealed interface ChatResult {
    data class Success(val message: String) : ChatResult
    data object NoInternet : ChatResult
    data object Timeout : ChatResult
    data class Error(val message: String) : ChatResult
}
