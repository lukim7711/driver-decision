package com.driverfinance.ui.screen.chat.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.driverfinance.ui.theme.Spacing

/**
 * Chat input bar at the bottom of the screen.
 * F008 spec: text field + send button (\u27A4).
 * Disabled when offline or loading.
 */
@Composable
fun ChatInputBar(
    text: String,
    enabled: Boolean,
    isOffline: Boolean,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.screenPadding, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = {
                Text(
                    text = if (isOffline) "Tidak ada koneksi internet" else "Ketik pertanyaan..."
                )
            },
            modifier = Modifier.weight(1f),
            enabled = !isOffline,
            singleLine = false,
            maxLines = 4,
            shape = RoundedCornerShape(Spacing.xl)
        )
        IconButton(
            onClick = onSend,
            enabled = enabled
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Kirim",
                tint = if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
