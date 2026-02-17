package com.driverfinance.ui.screen.chat

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.data.remote.dto.ChatMessage
import com.driverfinance.domain.usecase.chat.ChatMessageResult
import com.driverfinance.domain.usecase.chat.SendChatMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatBubble(
    val id: String,
    val content: String,
    val isUser: Boolean,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null
)

sealed interface ChatUiState {
    data object Offline : ChatUiState
    data class Active(
        val bubbles: List<ChatBubble>,
        val isSending: Boolean
    ) : ChatUiState
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Active(
        bubbles = listOf(
            ChatBubble(
                id = "greeting",
                content = "Halo! Saya AI asisten keuangan kamu.\n\nTanya apa saja soal performa kerja, hutang, target, atau keuangan kamu.",
                isUser = false
            )
        ),
        isSending = false
    ))
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // In-memory conversation history for LLM context (§6.3.3)
    private val conversationHistory = mutableListOf<ChatMessage>()
    private var messageCounter = 0

    init {
        checkConnectivity()
    }

    private fun checkConnectivity() {
        if (!isOnline()) {
            _uiState.value = ChatUiState.Offline
        }
    }

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        val currentState = _uiState.value as? ChatUiState.Active ?: return

        messageCounter++
        val userBubbleId = "user_$messageCounter"
        val aiBubbleId = "ai_$messageCounter"

        // Add user bubble + loading AI bubble
        val newBubbles = currentState.bubbles + listOf(
            ChatBubble(id = userBubbleId, content = trimmed, isUser = true),
            ChatBubble(id = aiBubbleId, content = "Sedang menganalisa data...", isUser = false, isLoading = true)
        )
        _uiState.value = ChatUiState.Active(bubbles = newBubbles, isSending = true)

        viewModelScope.launch {
            val result = sendChatMessageUseCase(
                userMessage = trimmed,
                conversationHistory = conversationHistory.toList()
            )

            val updatedBubbles = when (result) {
                is ChatMessageResult.Success -> {
                    // Add to conversation history for follow-up context
                    conversationHistory.add(ChatMessage(role = "user", content = trimmed))
                    conversationHistory.add(ChatMessage(role = "assistant", content = result.aiResponse))

                    // Trim to 20 messages max (§6.3.3)
                    while (conversationHistory.size > 20) {
                        conversationHistory.removeAt(0)
                    }

                    newBubbles.map {
                        if (it.id == aiBubbleId) it.copy(
                            content = result.aiResponse,
                            isLoading = false
                        ) else it
                    }
                }
                is ChatMessageResult.NoInternet -> {
                    newBubbles.map {
                        if (it.id == aiBubbleId) it.copy(
                            content = "Koneksi terputus. Coba kirim lagi.",
                            isLoading = false,
                            isError = true,
                            errorMessage = trimmed
                        ) else it
                    }
                }
                is ChatMessageResult.Timeout -> {
                    newBubbles.map {
                        if (it.id == aiBubbleId) it.copy(
                            content = "Gagal memproses. Coba lagi.",
                            isLoading = false,
                            isError = true,
                            errorMessage = trimmed
                        ) else it
                    }
                }
                is ChatMessageResult.Error -> {
                    newBubbles.map {
                        if (it.id == aiBubbleId) it.copy(
                            content = "Ada gangguan, coba beberapa saat lagi.",
                            isLoading = false,
                            isError = true,
                            errorMessage = trimmed
                        ) else it
                    }
                }
            }

            _uiState.value = ChatUiState.Active(bubbles = updatedBubbles, isSending = false)
        }
    }

    fun retry(originalMessage: String) {
        // Remove last error bubble, then resend
        val currentState = _uiState.value as? ChatUiState.Active ?: return
        val cleaned = currentState.bubbles.dropLast(1) // remove error AI bubble
        _uiState.value = ChatUiState.Active(bubbles = cleaned.dropLast(1), isSending = false) // also remove user bubble
        sendMessage(originalMessage)
    }

    private fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
