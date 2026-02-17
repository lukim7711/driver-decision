package com.driverfinance.domain.model

import java.util.UUID

/**
 * Domain models for F008 AI Chat.
 * Chat is session-only — no database persistence.
 * Conversation memory stored in ViewModel, cleared on navigation away.
 */

/** Who sent the message. */
enum class ChatRole {
    USER,
    AI
}

/** Message status for error/retry handling. */
enum class MessageStatus {
    SENT,
    LOADING,
    ERROR
}

/**
 * A single chat message.
 * AI messages support markdown-like formatting: **bold** for highlighted numbers.
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: ChatRole,
    val content: String,
    val status: MessageStatus = MessageStatus.SENT,
    val timestamp: Long = System.currentTimeMillis()
)

/** Connection state for the chat screen. */
enum class ConnectionState {
    ONLINE,
    OFFLINE
}

/**
 * Single state for F008 AI Chat screen.
 * CONSTITUTION: immutable data class, updated via copy().
 */
data class ChatScreenState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val connectionState: ConnectionState = ConnectionState.ONLINE,
    val errorMessage: String? = null
) {
    val canSend: Boolean
        get() = inputText.isNotBlank() && !isLoading && connectionState == ConnectionState.ONLINE

    val isOffline: Boolean
        get() = connectionState == ConnectionState.OFFLINE

    val isEmpty: Boolean
        get() = messages.isEmpty()

    val lastFailedMessageId: String?
        get() = messages.lastOrNull { it.status == MessageStatus.ERROR }?.id
}
