package com.driverfinance.ui.screen.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driverfinance.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("\uD83E\uDD16 AI Chat") }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        when (val state = uiState) {
            is ChatUiState.Offline -> {
                OfflineContent(modifier = Modifier.padding(innerPadding))
            }
            is ChatUiState.Active -> {
                ActiveChatContent(
                    bubbles = state.bubbles,
                    isSending = state.isSending,
                    onSend = viewModel::sendMessage,
                    onRetry = viewModel::retry,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun OfflineContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(Spacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("\uD83D\uDCE1", style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(Spacing.md))
        Text(
            text = "Butuh koneksi internet untuk chat dengan AI",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Text(
            text = "Fitur lain tetap bisa dipakai secara offline",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ActiveChatContent(
    bubbles: List<ChatBubble>,
    isSending: Boolean,
    onSend: (String) -> Unit,
    onRetry: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }

    // Auto-scroll to bottom on new messages
    LaunchedEffect(bubbles.size) {
        if (bubbles.isNotEmpty()) {
            listState.animateScrollToItem(bubbles.size - 1)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Chat messages
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            item { Spacer(modifier = Modifier.height(Spacing.sm)) }
            items(bubbles, key = { it.id }) { bubble ->
                ChatBubbleItem(
                    bubble = bubble,
                    onRetry = onRetry
                )
            }
            item { Spacer(modifier = Modifier.height(Spacing.sm)) }
        }

        // Input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ketik pertanyaan...") },
                enabled = !isSending,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputText.isNotBlank() && !isSending) {
                            onSend(inputText)
                            inputText = ""
                        }
                    }
                ),
                singleLine = false,
                maxLines = 4,
                shape = RoundedCornerShape(24.dp)
            )
            IconButton(
                onClick = {
                    if (inputText.isNotBlank() && !isSending) {
                        onSend(inputText)
                        inputText = ""
                    }
                },
                enabled = inputText.isNotBlank() && !isSending
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Kirim",
                    tint = if (inputText.isNotBlank() && !isSending)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ChatBubbleItem(
    bubble: ChatBubble,
    onRetry: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val alignment = if (bubble.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (bubble.isUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else if (bubble.isError) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (bubble.isUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else if (bubble.isError) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (bubble.isUser) 16.dp else 4.dp,
                    bottomEnd = if (bubble.isUser) 4.dp else 16.dp
                ))
                .background(bgColor)
                .padding(Spacing.md)
        ) {
            if (!bubble.isUser && !bubble.isLoading && !bubble.isError) {
                Text(
                    text = "\uD83E\uDD16",
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (bubble.isLoading) {
                Text(
                    text = "\uD83E\uDD16 \u25CF\u25CF\u25CF",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
                Text(
                    text = bubble.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.7f)
                )
            } else {
                Text(
                    text = bubble.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }

            if (bubble.isError && bubble.errorMessage != null) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                TextButton(
                    onClick = { onRetry(bubble.errorMessage) }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp))
                    Text("Coba lagi")
                }
            }
        }
    }
}
