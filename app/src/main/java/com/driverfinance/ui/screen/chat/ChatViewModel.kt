package com.driverfinance.ui.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.model.ChatMessage
import com.driverfinance.domain.model.ChatRole
import com.driverfinance.domain.model.ChatScreenState
import com.driverfinance.domain.model.ConnectionState
import com.driverfinance.domain.model.MessageStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for F008 AI Chat.
 *
 * Conversation memory: all messages in _screenState.messages (session-only).
 * F008 spec: max 20 messages (10 pairs). Cleared on navigation away.
 *
 * TODO: Inject:
 *   - GroqChatRepository (PR #4) for actual LLM calls
 *   - Context builders that read from Room (F001-F007, F009 tables)
 *   - NetworkMonitor for real connectivity state
 */
@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    companion object {
        const val MAX_HISTORY_MESSAGES = 20
        private const val TIMEOUT_MS = 30_000L
        private const val GREETING_MESSAGE = "Halo! Saya AI asisten keuangan kamu.\n\n" +
                "Tanya apa saja soal performa kerja, hutang, target, atau keuangan kamu."
    }

    private val _screenState = MutableStateFlow(ChatScreenState())
    val screenState: StateFlow<ChatScreenState> = _screenState.asStateFlow()

    // ---- Input ----

    fun updateInput(text: String) {
        _screenState.update { it.copy(inputText = text) }
    }

    // ---- Send ----

    fun sendMessage() {
        val state = _screenState.value
        if (!state.canSend) return

        val userMessage = ChatMessage(
            role = ChatRole.USER,
            content = state.inputText.trim()
        )

        val loadingMessage = ChatMessage(
            role = ChatRole.AI,
            content = "",
            status = MessageStatus.LOADING
        )

        _screenState.update { current ->
            val updated = current.messages + userMessage + loadingMessage
            current.copy(
                messages = updated.takeLast(MAX_HISTORY_MESSAGES),
                inputText = "",
                isLoading = true
            )
        }

        viewModelScope.launch {
            try {
                // TODO: Replace with actual Groq API call:
                //   1. Build context from local DB (today's data, debts, target, etc.)
                //   2. Build conversation history (past messages)
                //   3. Call groqChatRepository.chat(systemPrompt, context, messages)
                //   4. Parse response
                delay(1500) // Simulate API latency

                val aiResponse = generatePlaceholderResponse(userMessage.content)

                val aiMessage = ChatMessage(
                    role = ChatRole.AI,
                    content = aiResponse
                )

                _screenState.update { current ->
                    val withoutLoading = current.messages.filter { it.status != MessageStatus.LOADING }
                    current.copy(
                        messages = (withoutLoading + aiMessage).takeLast(MAX_HISTORY_MESSAGES),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Chat API error")
                _screenState.update { current ->
                    val withError = current.messages.map {
                        if (it.status == MessageStatus.LOADING) {
                            it.copy(
                                content = "Gagal memproses. Coba lagi.",
                                status = MessageStatus.ERROR
                            )
                        } else it
                    }
                    current.copy(
                        messages = withError,
                        isLoading = false,
                        errorMessage = "Koneksi terputus. Coba kirim lagi."
                    )
                }
            }
        }
    }

    // ---- Retry ----

    fun retryLastMessage() {
        _screenState.update { current ->
            val errorMsg = current.messages.lastOrNull { it.status == MessageStatus.ERROR }
            if (errorMsg != null) {
                val withoutError = current.messages.filter { it.id != errorMsg.id }
                current.copy(messages = withoutError, errorMessage = null)
            } else current
        }
        // Re-send the last user message
        val lastUserMsg = _screenState.value.messages.lastOrNull { it.role == ChatRole.USER }
        if (lastUserMsg != null) {
            _screenState.update { it.copy(inputText = lastUserMsg.content) }
            sendMessage()
        }
    }

    // ---- Clear ----

    fun clearChat() {
        _screenState.update { ChatScreenState() }
    }

    // ---- Connection ----

    fun updateConnectionState(isOnline: Boolean) {
        _screenState.update {
            it.copy(connectionState = if (isOnline) ConnectionState.ONLINE else ConnectionState.OFFLINE)
        }
    }

    fun dismissError() {
        _screenState.update { it.copy(errorMessage = null) }
    }

    /**
     * Placeholder response until Groq API is connected.
     * Returns a demo response to show UI works.
     */
    private fun generatePlaceholderResponse(userInput: String): String {
        return "Fitur AI Chat sedang dalam pengembangan. " +
                "Nanti saya bisa menjawab pertanyaan seputar performa kerja, " +
                "hutang, target harian, dan keuangan kamu berdasarkan data aktual.\n\n" +
                "Pertanyaan kamu: \"$userInput\" \u2014 akan saya proses saat data sudah terhubung \uD83D\uDE0A"
    }
}
