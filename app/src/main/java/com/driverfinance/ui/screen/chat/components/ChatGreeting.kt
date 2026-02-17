package com.driverfinance.ui.screen.chat.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.driverfinance.ui.theme.Spacing

/**
 * Greeting message shown when chat is first opened.
 * F008 spec mockup A:
 *   \uD83E\uDD16
 *   Halo! Saya AI asisten keuangan kamu.
 *   Tanya apa saja soal performa kerja, hutang, target, atau keuangan kamu.
 */
@Composable
fun ChatGreeting() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "\uD83E\uDD16",
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        Text(
            text = "Halo! Saya AI asisten keuangan kamu.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = "Tanya apa saja soal performa kerja, hutang, target, atau keuangan kamu.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
