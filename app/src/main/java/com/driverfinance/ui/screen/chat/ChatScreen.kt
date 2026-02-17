package com.driverfinance.ui.screen.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.driverfinance.domain.model.ChatRole
import com.driverfinance.domain.model.MessageStatus
import com.driverfinance.ui.screen.chat.components.AiBubble
import com.driverfinance.ui.screen.chat.components.ChatGreeting
import com.driverfinance.ui.screen.chat.components.ChatHeader
import com.driverfinance.ui.screen.chat.components.ChatInputBar
import com.driverfinance.ui.screen.chat.components.LoadingBubble
import com.driverfinance.ui.screen.chat.components.OfflineState
import com.driverfinance.ui.screen.chat.components.UserBubble
import com.driverfinance.ui.theme.Spacing

/**
 * F008 — AI Chat Screen.
 *
 * Layout: Header → Messages (LazyColumn, weight 1f) → InputBar
 * Fresh chat every session. No DB persistence.
 */
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ChatHeader()

        if (state.isOffline) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                OfflineState()
            }
        } else {
            // Messages
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenPadding)
            ) {
                // Greeting always first
                item(key = "greeting") {
                    Spacer(modifier = Modifier.height(Spacing.lg))
                    ChatGreeting()
                    Spacer(modifier = Modifier.height(Spacing.lg))
                }

                items(state.messages, key = { it.id }) { message ->
                    when {
                        message.status == MessageStatus.LOADING -> {
                            LoadingBubble()
                        }

                        message.role == ChatRole.USER -> {
                            UserBubble(text = message.content)
                        }

                        message.role == ChatRole.AI -> {
                            AiBubble(
                                text = message.content,
                                isError = message.status == MessageStatus.ERROR,
                                onRetry = if (message.status == MessageStatus.ERROR) {
                                    viewModel::retryLastMessage
                                } else null
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.md))
                }
            }
        }

        // Input bar
        ChatInputBar(
            text = state.inputText,
            enabled = state.canSend,
            isOffline = state.isOffline,
            onTextChange = viewModel::updateInput,
            onSend = viewModel::sendMessage
        )
    }
}
